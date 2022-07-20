package cn.smallyoung.commonsecurityspringbootstarter.aspect;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.DynaBean;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.system.SystemUtil;
import cn.smallyoung.commonsecurityspringbootstarter.entity.SysOperationLog;
import cn.smallyoung.commonsecurityspringbootstarter.enums.SysOperationLogWayEnum;
import cn.smallyoung.commonsecurityspringbootstarter.interfaces.DataName;
import cn.smallyoung.commonsecurityspringbootstarter.interfaces.SystemOperationLog;
import cn.smallyoung.commonsecurityspringbootstarter.service.SysOperationLogService;
import cn.smallyoung.commonsecurityspringbootstarter.util.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author smallyoung
 */
@Slf4j
@Aspect
@Component
public class SysOperationLogAspect {

    @Value("${server-ip-address:''}")
    private String serverIpAddress;

    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private SysOperationLogService sysOperationLogService;

    @Around("@annotation(systemOperationLog)")
    public Object around(ProceedingJoinPoint pjp, SystemOperationLog systemOperationLog) throws Throwable {
        SysOperationLog sysOperationLog = new SysOperationLog();
        Signature signature = pjp.getSignature();
        if (signature != null) {
            sysOperationLog.setPackageAndMethod(signature.getDeclaringTypeName() + "." + signature.getName());
        }
        sysOperationLog.setStartTime(LocalDateTime.now());
        sysOperationLog.setMethod(systemOperationLog.methods());
        sysOperationLog.setModule(systemOperationLog.module());
        sysOperationLog.setServerIp("".equals(serverIpAddress) ? SystemUtil.getHostInfo().getAddress() : serverIpAddress);
        sysOperationLog.setRequestIp(RequestUtils.getIp());
        JSONObject params = getParam(pjp);
        sysOperationLog.setParams(params);
        Object value = BeanUtil.getProperty(params, systemOperationLog.parameterKey());
        if (systemOperationLog.way() != SysOperationLogWayEnum.RecordOnly) {
            Object oldObject = getOperateBeforeDataByParamType(systemOperationLog.serviceClass(),
                    systemOperationLog.queryMethod(), value, systemOperationLog.parameterType());
            sysOperationLog.setBeforeData(getContent(oldObject, new HashSet<>(), null));
        }
        //执行service
        try {
            Object object = pjp.proceed();
            if (SysOperationLogWayEnum.UserAfter == systemOperationLog.way() && object != null) {
                sysOperationLog.setAfterData(getContent(object, new HashSet<>(), null));
                sysOperationLog.setContent(compareJson(sysOperationLog.getBeforeData(), sysOperationLog.getAfterData()));
            } else if (systemOperationLog.way() != SysOperationLogWayEnum.RecordOnly) {
                Object newObject = getOperateBeforeDataByParamType(systemOperationLog.serviceClass(),
                        systemOperationLog.queryMethod(), value, systemOperationLog.parameterType());
                if (newObject != null) {
                    sysOperationLog.setAfterData(getContent(newObject, new HashSet<>(), null));
                    sysOperationLog.setContent(compareJson(sysOperationLog.getBeforeData(), sysOperationLog.getAfterData()));
                }
            }
            sysOperationLog.setEndTime(LocalDateTime.now());
            sysOperationLog.setResultStatus("SUCCESS");
            sysOperationLogService.save(sysOperationLog);
            return object;
        } catch (Exception e) {
            sysOperationLog.setResultMsg(e.getMessage());
            sysOperationLog.setResultStatus("ERROR");
            sysOperationLog.setEndTime(LocalDateTime.now());
            sysOperationLogService.save(sysOperationLog);
            throw e;
        }
    }

    public JSONObject getParam(ProceedingJoinPoint pjp) {
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        String[] fieldsName = methodSignature.getParameterNames();
        if (fieldsName == null || fieldsName.length <= 0) {
            return null;
        }
        // 拦截的方法参数
        Object[] args = pjp.getArgs();
        JSONObject result = new JSONObject();
        Object obj;
        for (int i = 0; i < args.length; i++) {
            obj = args[i];
            if (obj != null) {
                if (ClassUtil.isSimpleValueType(obj.getClass())) {
                    result.set(fieldsName[i], args[i]);
                } else if (obj instanceof JSONArray) {
                    result.set(fieldsName[i], (new JSONObject()).set("param", obj));
                } else if (obj instanceof JSONObject) {
                    result.set(fieldsName[i], obj);
                } else {
                    result.set(fieldsName[i], new JSONObject(obj));
                }
            } else {
                result.set(fieldsName[i], null);
            }
        }
        return result;
    }

    /**
     * 根据反射查询实体bean
     *
     * @param serviceClass  查询bean的服务类
     * @param queryMethod   查询单个详情的bean的方法
     * @param value         参数值
     * @param parameterType 查询详情的参数类型
     * @return 查询到的对象
     */
    private Object getOperateBeforeDataByParamType(Class<?> serviceClass, String queryMethod, Object value, Class<?> parameterType) {
        if (value == null || StrUtil.hasBlank(queryMethod) || serviceClass == null) {
            return null;
        }
        Object object = applicationContext.getBean(serviceClass);
        return DynaBean.create(object).invoke(queryMethod, Convert.convert(parameterType, value));
    }

    /**
     * 将实体对象转换成json。仅标注了@data注解的才会被转换
     *
     * @param object        需要转换的实体对象
     * @param set           排重，避免循环引用问题，排重采用对象的hashCode值判断
     * @param dataNameValue 需要转换的字段
     * @return 转换后的json
     */
    public JSONObject getContent(Object object, Set<Integer> set, String dataNameValue) {
        if (object == null) {
            return null;
        }
        if (set == null) {
            set = new HashSet<>();
        }
        set.add(object.hashCode());
        //基本类型或者是Map、json类型
        if (ClassUtil.isSimpleValueType(object.getClass()) || object instanceof Map) {
            return new JSONObject(object);
        }
        //集合
        JSONObject result = new JSONObject();
        if (object instanceof Collection) {
            JSONArray jsonArray = new JSONArray();
            JSONObject content;
            for (Object obj : (Collection<?>) object) {
                if (!set.contains(obj.hashCode())) {
                    set.add(obj.hashCode());
                    content = getContent(obj, set, dataNameValue);
                    if (content != null && content.keySet().size() > 0) {
                        jsonArray.add(content);
                    }
                }
            }
            result.putOpt("data", jsonArray);
            return result;
        }
        Field[] fields = ReflectUtil.getFields(object.getClass());
        Object value;
        DataName dataName;
        JSONObject content;
        JSONArray ids;
        Id id;
        EmbeddedId embeddedId;
        boolean notId;
        Field[] fields2;
        boolean hasValue = dataNameValue != null && !"".equals(dataNameValue);
        for (Field field : fields) {
            if ("serialVersionUID".equals(field.getName())) {
                continue;
            }
            dataName = AnnotationUtil.getAnnotation(field, DataName.class);
            id = AnnotationUtil.getAnnotation(field, Id.class);
            embeddedId = AnnotationUtil.getAnnotation(field, EmbeddedId.class);
            notId = (id == null && embeddedId == null);
            if (notId) {
                if (dataName == null && !hasValue) {
                    continue;
                }
                if (!hasValue) {
                    dataNameValue = dataName.value();
                    hasValue = dataNameValue != null && !"".equals(dataNameValue);
                }
                if (hasValue && Arrays.stream(dataNameValue.split(",")).noneMatch(v -> v.split("\\.", 2)[0].equals(field.getName()))) {
                    continue;
                }
            }

            if (!notId) {
                ids = result.getJSONArray("primaryKey");
                if (ids == null) {
                    ids = new JSONArray();
                }
                content = new JSONObject();
                value = ReflectUtil.getFieldValue(object, field);
                if (value == null) {
                    continue;
                }
                if (ClassUtil.isSimpleValueType(value.getClass())) {
                    content.putOpt(field.getName(), value);
                } else {
                    fields2 = ReflectUtil.getFields(value.getClass());
                    for (Field field2 : fields2) {
                        content.putOpt(field2.getName(), ReflectUtil.getFieldValue(value, field2));
                    }
                }
                ids.add(content);
                result.putOpt("primaryKey", ids);
            } else {
                content = new JSONObject();
                if (dataName != null) {
                    content.putOpt("name", dataName.name());
                }
                value = ReflectUtil.getFieldValue(object, field);
                if (value != null) {
                    if (ClassUtil.isSimpleValueType(value.getClass()) || value instanceof Map) {
                        content.putOpt("value", value);
                    } else if (value instanceof Collection) {
                        if (set.contains(value.hashCode())) {
                            continue;
                        }
                        dataNameValue = getDataNameValue(dataNameValue);
                        content.putOpt("value", getContent(value, set, dataNameValue).get("data"));
                    } else {
                        if (set.contains(value.hashCode())) {
                            continue;
                        }
                        dataNameValue = getDataNameValue(dataNameValue);
                        content.putOpt("value", getContent(value, set, dataNameValue));
                    }
                } else {
                    content.putOpt("value", null);
                }
                result.putOpt(field.getName(), content);
            }
        }
        return result;
    }

    /**
     * 获取下一级标注的@data值，例如role.id--> id
     *
     * @param dataNameValue 标注的值
     * @return 处理后的值
     */
    private String getDataNameValue(String dataNameValue) {
        if (dataNameValue == null || "".equals(dataNameValue)) {
            return null;
        }
        return Arrays.stream(dataNameValue.split(",")).map(s -> s.split("\\.", 2).length == 2 ? s.split("\\.", 2)[1] : s).collect(Collectors.joining(","));
    }

    /**
     * 内容变更说明，对比两个json，生成新对比说明
     *
     * @param json1 变化前对象的json
     * @param json2 变化后对象的json
     * @return 两个json的差异
     */
    private JSONObject compareJson(JSONObject json1, JSONObject json2) {
        if (json1 == null) {
            json2.putOpt("operation", "新增");
            return json2;
        }
        if (json2 == null) {
            json1.putOpt("operation", "删除");
            return json1;
        }
        JSONObject result = new JSONObject();
        //获取所有的key
        Set<String> allKey = new HashSet<>();
        allKey.addAll(json1.keySet());
        allKey.addAll(json2.keySet());
        //循环比较
        JSONObject compare;
        Object obj1;
        Object val1;
        Object obj2;
        Object val2;
        for (String key : allKey) {
            //如果是主键则跳过
            if ("primaryKey".equals(key)) {
                continue;
            }
            obj1 = json1.get(key);
            obj2 = json2.get(key);
            if (obj1 == null && obj2 == null) {
                continue;
            }
            if (obj1 == null) {
                compare = new JSONObject();
                compare.putOpt("operation", "新增");
                compare.putOpt("name", ((JSONObject) obj2).get("name"));
                compare.putOpt("newValue", ((JSONObject) obj2).get("value"));
                result.putOpt(key, compare);
                continue;
            }
            if (obj2 == null) {
                compare = new JSONObject();
                compare.putOpt("operation", "删除");
                compare.putOpt("name", ((JSONObject) obj1).get("name"));
                compare.putOpt("oldValue", ((JSONObject) obj1).get("value"));
                result.putOpt(key, compare);
                continue;
            }
            val1 = ((JSONObject) obj1).get("value");
            val2 = ((JSONObject) obj2).get("value");
            if (Objects.equals(val1, val2)) {
                continue;
            }
            compare = new JSONObject();
            compare.putOpt("name", ((JSONObject) obj1).get("name"));
            if (val1 instanceof JSONArray) {
                compare.putOpt("compare", compareJSONArray((JSONArray) val1, (JSONArray) val2));
            } else {
                compare.putOpt("operation", "修改");
                compare.putOpt("oldValue", val1);
                compare.putOpt("newValue", val2);
            }
            result.putOpt(key, compare);
        }
        return result;
    }


    /**
     * 比较两个列表的差异，对比标准是根据对象的id主键，及生成json后的primaryKey的值
     *
     * @param jsonArray1 变化前的json
     * @param jsonArray2 变化后的json
     * @return 对比后的差异
     */
    private JSONArray compareJSONArray(JSONArray jsonArray1, JSONArray jsonArray2) {
        JSONArray result = new JSONArray();
        //两个列表相同部分
        JSONArray common = jsonArray1.stream().filter(jsonArray2::contains).collect(Collector.of(JSONArray::new, JSONArray::put, JSONArray::put));
        //分别排除两个列表的相同部分
        JSONArray array1 = jsonArray1.stream().filter(a -> !common.contains(a)).collect(Collector.of(JSONArray::new, JSONArray::put, JSONArray::put));
        JSONArray array2 = jsonArray2.stream().filter(a -> !common.contains(a)).collect(Collector.of(JSONArray::new, JSONArray::put, JSONArray::put));
        //处理数据，方便对比
        JSONObject jsonObject;
        Map<JSONArray, JSONObject[]> data = new HashMap<>();
        JSONObject[] array;
        for (Object o : array1) {
            jsonObject = (JSONObject) o;
            array = new JSONObject[2];
            array[0] = jsonObject;
            data.put(jsonObject.getJSONArray("primaryKey"), array);
        }
        for (Object o : array2) {
            jsonObject = (JSONObject) o;
            array = data.get(jsonObject.getJSONArray("primaryKey"));
            if (array == null) {
                array = new JSONObject[2];
            }
            array[1] = jsonObject;
            data.put(jsonObject.getJSONArray("primaryKey"), array);
        }
        //比较两个列表
        for (Map.Entry<JSONArray, JSONObject[]> entry : data.entrySet()) {
            array = entry.getValue();
            if (array[0] == null) {
                jsonObject = array[1];
                jsonObject.putOpt("operation", "新增");
            } else if (array[1] == null) {
                jsonObject = array[0];
                jsonObject.putOpt("operation", "删除");
            } else {
                jsonObject = new JSONObject();
                jsonObject.putOpt("operation", "编辑");
                jsonObject.putOpt("oldValue", array[0]);
                jsonObject.putOpt("newValue", array[1]);
            }
            result.put(jsonObject);
        }
        return result;
    }
}
