

Java为数据结构中的映射定义了一个接口java.util.Map，此接口主要有四个常用的实现类，分别是HashMap、Hashtable、LinkedHashMap和TreeMap，类继承关系如下图所示：
![](http://tech.meituan.com/img/java-hashmap/java.util.map%E7%B1%BB%E5%9B%BE.png)

下面针对各个实现类的特点做一些说明：
(1) HashMap：它根据键的hashCode值存储数据，大多数情况下可以直接定位到它的值，因而具有很快的访问速度，但遍历顺序却是不确定的。 HashMap最多只允许一条记录的键为null，允许多条记录的值为null。HashMap非线程安全，即任一时刻可以有多个线程同时写 HashMap，可能会导致数据的不一致。如果需要满足线程安全，可以用 Collections的synchronizedMap方法使HashMap具有线程安全的能力，或者使用ConcurrentHashMap。
(2) Hashtable：Hashtable是遗留类，很多映射的常用功能与HashMap类似，不同的是它承自Dictionary类，并且是线程安全的， 任一时间只有一个线程能写Hashtable，并发性不如ConcurrentHashMap，因为ConcurrentHashMap引入了分段锁。 Hashtable不建议在新代码中使用，不需要线程安全的场合可以用HashMap替换，需要线程安全的场合可以用ConcurrentHashMap 替换。
(3) LinkedHashMap：LinkedHashMap是HashMap的一个子类，保存了记录的插入顺序，在用Iterator遍历LinkedHashMap时，先得到的记录肯定是先插入的，也可以在构造时带参数，按照访问次序排序。
(4) TreeMap：TreeMap实现SortedMap接口，能够把它保存的记录根据键排序，默认是按键值的升序排序，也可以指定排序的比较器，当用 Iterator遍历TreeMap时，得到的记录是排过序的。如果使用排序的映射，建议使用TreeMap。在使用TreeMap时，key必须实现 Comparable接口或者在构造TreeMap传入自定义的Comparator，否则会在运行时抛出 java.lang.ClassCastException类型的异常。
对于上述四种Map类型的类，要求映射中的key是不可变对象。不可变对象是该对象在创建后它的哈希值不会被改变。如果对象的哈希值发生变化，Map对象很可能就定位不到映射的位置了。
通过上面的比较，我们知道了HashMap是Java的Map家族中一个普通成员，鉴于它可以满足大多数场景的使用条件，所以是使用频度最高的一个。下文我们主要结合源码，从存储结构、常用方法分析、扩容以及安全性等方面深入讲解HashMap的工作原理。
# HashMap
## Sort:排序
对TreeMap按照value值升序: 
```
List<Map.Entry<String,String>> mappingList = null; 
  Map<String,String> map = new TreeMap<String,String>(); 
  map.put("aaaa", "month"); 
  map.put("bbbb", "bread"); 
  map.put("ccccc", "attack"); 

  //通过ArrayList构造函数把map.entrySet()转换成list 
  mappingList = new ArrayList<Map.Entry<String,String>>(map.entrySet()); 
  //通过比较器实现比较排序 
  Collections.sort(mappingList, new Comparator<Map.Entry<String,String>>(){ 
   public int compare(Map.Entry<String,String> mapping1,Map.Entry<String,String> mapping2){ 
    return mapping1.getValue().compareTo(mapping2.getValue()); 
   } 
  }); 

  for(Map.Entry<String,String> mapping:mappingList){ 
   System.out.println(mapping.getKey()+":"+mapping.getValue()); 
  } 
```
对HashMap(或Hashtable,LinkedHashMap)按照key的值升序: 
```
List<Map.Entry<String,String>> mappingList = null; 
  Map<String,String> map = new HashMap<String,String>(); 
  map.put("month", "month"); 
  map.put("bread", "bread"); 
  map.put("attack", "attack"); 

  //通过ArrayList构造函数把map.entrySet()转换成list 
  mappingList = new ArrayList<Map.Entry<String,String>>(map.entrySet()); 
  //通过比较器实现比较排序 
  Collections.sort(mappingList, new Comparator<Map.Entry<String,String>>(){ 
   public int compare(Map.Entry<String,String> mapping1,Map.Entry<String,String> mapping2){ 
    return mapping1.getKey().compareTo(mapping2.getKey()); 
   } 
  }); 

  for(Map.Entry<String,String> mapping:mappingList){ 
   System.out.println(mapping.getKey()+":"+mapping.getValue()); 
  } 
``` 

# HashMap
- [Java HashMap原理解析](https://github.com/HelloListen/Secret/blob/master/content/post/2016/05/java-hashmap-hashcode-hash.md)

## Index&Traversal:索引遍历
### 排序:Sort
#### 按键排序
```
    public Map<String, String> sortMapByKey(Map<String, String> oriMap) {  
        if (oriMap == null || oriMap.isEmpty()) {  
            return null;  
        }  
        Map<String, String> sortedMap = new TreeMap<String, String>(new Comparator<String>() {  
            public int compare(String key1, String key2) {  
                int intKey1 = 0, intKey2 = 0;  
                try {  
                    intKey1 = getInt(key1);  
                    intKey2 = getInt(key2);  
                } catch (Exception e) {  
                    intKey1 = 0;   
                    intKey2 = 0;  
                }  
                return intKey1 - intKey2;  
            }});  
        sortedMap.putAll(oriMap);  
        return sortedMap;  
    }  
      
    private int getInt(String str) {  
        int i = 0;  
        try {  
            Pattern p = Pattern.compile("^\\d+");  
            Matcher m = p.matcher(str);  
            if (m.find()) {  
                i = Integer.valueOf(m.group());  
            }  
        } catch (NumberFormatException e) {  
            e.printStackTrace();  
        }  
        return i;  
    }  
```
#### 按值排序
```
    public Map<String, String> sortMapByValue(Map<String, String> oriMap) {  
        Map<String, String> sortedMap = new LinkedHashMap<String, String>();  
        if (oriMap != null && !oriMap.isEmpty()) {  
            List<Map.Entry<String, String>> entryList = new ArrayList<Map.Entry<String, String>>(oriMap.entrySet());  
            Collections.sort(entryList,  
                    new Comparator<Map.Entry<String, String>>() {  
                        public int compare(Entry<String, String> entry1,  
                                Entry<String, String> entry2) {  
                            int value1 = 0, value2 = 0;  
                            try {  
                                value1 = getInt(entry1.getValue());  
                                value2 = getInt(entry2.getValue());  
                            } catch (NumberFormatException e) {  
                                value1 = 0;  
                                value2 = 0;  
                            }  
                            return value2 - value1;  
                        }  
                    });  
            Iterator<Map.Entry<String, String>> iter = entryList.iterator();  
            Map.Entry<String, String> tmpEntry = null;  
            while (iter.hasNext()) {  
                tmpEntry = iter.next();  
                sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());  
            }  
        }  
        return sortedMap;  
    }  ```

# LinkedHashMap
LinkedHashMap会保留插入的顺序。