package cn.smallyoung.commonsecurityspringbootstarter.funcion;

/**
 * @author smallyoung
 */
@FunctionalInterface
public interface Function3Parameter<T, U, W, R> {
    /**
     * 执行三个参数的函数，并返回另外类型的值
     *
     * @param t 参数类型1
     * @param u 参数类型2
     * @param w 参数类型3
     * @return 返回规定的泛型
     */
    R apply(T t, U u, W w);
}
