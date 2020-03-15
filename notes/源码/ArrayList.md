######
- 实现了List接口
- 非线程安全的集合
- 对应的线程安全集合为Vector
- 将ArrayList变为线程安全list的方法
```
List list = Collections.synchronizedList(new ArrayList(...));
```
- 默认初始容量
```
private static final int DEFAULT_CAPACITY = 10;
```


###### 构造方法
- 无参构造 - 指定elementData为java.util.ArrayList#DEFAULTCAPACITY_EMPTY_ELEMENTDATA
- 参数为int initialCapacity的构造 - 根据条件指定elementData;入参大于0时，elementData=new Object[initialCapacity]

###### add方法调用时，扩容
- 最终调用的就是这个grow方法
- 扩容时，会将将自己该扩容为当前elementData数组容量的1.5倍
- size就是当前list存储元素的个数
```
public boolean add(E e) {
    ensureCapacityInternal(size + 1);  // Increments modCount!!
    elementData[size++] = e;
    return true;
}
```
....
如果集合非空，且size+1>当前capacity（即elementData.length）容量，则需要执行下边方法，扩容elementData数组
....
```    
private void grow(int minCapacity) {
    // overflow-conscious code
    int oldCapacity = elementData.length;
    int newCapacity = oldCapacity + (oldCapacity >> 1);
    if (newCapacity - minCapacity < 0)
        newCapacity = minCapacity;
    if (newCapacity - MAX_ARRAY_SIZE > 0)
        newCapacity = hugeCapacity(minCapacity);
    // minCapacity is usually close to size, so this is a win:
    elementData = Arrays.copyOf(elementData, newCapacity);
}
```

###### others
- System.arraycopy
