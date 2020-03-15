###### 
- 实现了map接口
- 允许空的键和空的值
- 非线程安全
- HashTable线程安全
- 默认初始容量
```
static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
```
- 默认负载因子
```
/**
 * The load factor used when none specified in constructor.
 */
static final float DEFAULT_LOAD_FACTOR = 0.75f;
```
- 保存数据的结构 - 数组+链表（或数组+红黑树）
- 保存数据的数组
```
/**
 * The table, initialized on first use, and resized as
 * necessary. When allocated, length is always a power of two.
 * (We also tolerate length zero in some operations to allow
 * bootstrapping mechanics that are currently not needed.)
 */
transient Node<K,V>[] table;
```
- 触发扩容的点。当目前存储元素数组个数，大于该值，则触发扩容。
```
/**
 * The next size value at which to resize (capacity * load factor).
 *
 * @serial
 */
// (The javadoc description is true upon serialization.
// Additionally, if the table array has not been allocated, this
// field holds the initial array capacity, or zero signifying
// DEFAULT_INITIAL_CAPACITY.)
int threshold;
```
- 扩容的时候，1.7及之前，都是用下边的方法插入数据的，所以可能会导致多线程扩容死循环的出现
```
/**
 * Transfers all entries from current table to newTable.
 */
void transfer(Entry[] newTable) {
    Entry[] src = table;
    int newCapacity = newTable.length;
    for (int j = 0; j < src.length; j++) {
        Entry<K,V> e = src[j];
        if (e != null) {
            src[j] = null;
            do {
                Entry<K,V> next = e.next;
                int i = indexFor(e.hash, newCapacity);
                e.next = newTable[i];
                newTable[i] = e;
                e = next;
            } while (e != null);
        }
    }
}
```
- 1.8之后的扩容已经改进，如下(省略的与链表重新分配、入值逻辑关系不特大的地方)
```
1、无节点，不处理；
2、单节点，重新计算index（hash & (newCap - 1)）。
3、多节点，跟单节同样的情况，只是没有重新计算所有的index，而是看看原来的hash值新增的那个bit是1还是0（因为容量扩大了一倍，因此影响结果的是hash之前没有参与运算的最右侧位值，通过 hash & oldCap 便能得到），是0的话索引没变，是1的话索引变成“原索引+oldCap”。

作者：spiritTalk
链接：https://www.jianshu.com/p/f2361d06da82
来源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
```
```
...
Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
table = newTab;
if (oldTab != null) {
    for (int j = 0; j < oldCap; ++j) {
        Node<K,V> e;
        if ((e = oldTab[j]) != null) {
            oldTab[j] = null;
            if (e.next == null)
                newTab[e.hash & (newCap - 1)] = e;
            else if (e instanceof TreeNode)
                ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
            else { // preserve order
                Node<K,V> loHead = null, loTail = null;
                Node<K,V> hiHead = null, hiTail = null;
                Node<K,V> next;
                do {
                    next = e.next;
                    if ((e.hash & oldCap) == 0) {
                        if (loTail == null)
                            loHead = e;
                        else
                            loTail.next = e;
                        loTail = e;
                    }
                    else {
                        if (hiTail == null)
                            hiHead = e;
                        else
                            hiTail.next = e;
                        hiTail = e;
                    }
                } while ((e = next) != null);
                if (loTail != null) {
                    loTail.next = null;
                    newTab[j] = loHead;
                }
                if (hiTail != null) {
                    hiTail.next = null;
                    newTab[j + oldCap] = hiHead;
                }
            }
        }
    }
}
...
```
