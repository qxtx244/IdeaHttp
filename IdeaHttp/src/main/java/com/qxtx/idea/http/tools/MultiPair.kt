package com.qxtx.idea.http.tools

inline fun <K, V> MultiPair<K, V>.forEach(action: (Map.Entry<K, V>) -> Unit) {
    getAll().entries.forEach { entities -> action(entities) }
}

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/5/10 11:31
 *
 * **Description**
 *
 * 复数键值对实现，非线程安全
 *
 * @param K 索引类型
 * @param V 值类型
 */
class MultiPair<K, V> {

    private val map = LinkedHashMap<K, V>()

    operator fun get(key: K) = map[key]

    operator fun set(key: K, value: V) = add(key, value)

    operator fun plusAssign(headers: MultiPair<K, V>) {
        add(headers)
    }

    operator fun minusAssign(key: K) {
        remove(key)
    }

    /**
     * 添加元素
     * @param key 元素的索引
     * @param value 元素的值
     * @return [MultiPair]对象
     */
    fun add(key: K, value: V): MultiPair<K, V> {
        map[key] = value
        return this
    }

    /**
     * 添加元素
     * @param element 被添加的元素
     * @return [MultiPair]对象
     */
    fun add(element: MultiPair<K, V>): MultiPair<K, V> {
        map.putAll(element.map)
        return this
    }

    /**
     * 移除指定元素
     * @param key 目标元素的索引
     */
    fun remove(key: K) = map.remove(key)

    /** 清除所有元素 */
    fun clear() = map.clear()

    /**
     * 重置对象
     * @return [MultiPair]对象
     */
    fun reset(): MultiPair<K, V> {
        clear()
        return this
    }

    /**
     * 获取当前的所有元素
     * @return 当前的元素集
     */
    fun getAll() = map

    /**
     * 检查当前是否无元素
     * @return true表示没有任何元素，false表示非空
     */
    fun isEmpty() = map.isEmpty()

    /**
     * 获取当前元素的数量
     * @return 当前元素数量
     */
    fun size() = map.size
}