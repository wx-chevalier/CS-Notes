# Java HashMap

HashMap 由数组和链表组成的，数组是 HashMap 的主体，链表则是主要为了解决哈希冲突而存在的。如果定位到的数组位置不含链表，那么查找、添加等操作很快，仅需一次寻址即可，其时间复杂度为 O(1)；如果定位到的数组包含链表，对于添加操作，其时间复杂度为 O(n)——首先遍历链表，存在即覆盖，不存在则新增；对于查找操作来讲，仍需要遍历链表，然后通过 key 对象的 equals 方法逐一对比查找。从性能上考虑，HashMap 中的链表出现越少，即哈希冲突越少，性能也就越好。所以，在日常编码中，可以使用 HashMap 存取键值映射关系。

# 应用示例

## 数组转化为树

```java
/** 菜单DO类 */
@Setter
@Getter
@ToString
public static class MenuDO {
    /** 菜单标识 */
    private Long id;
    /** 菜单父标识 */
    private Long parentId;
    /** 菜单名称 */
    private String name;
    /** 菜单链接 */
    private String url;
}

/** 菜单VO类 */
@Setter
@Getter
@ToString
public static class MenuVO {
    /** 菜单标识 */
    private Long id;
    /** 菜单名称 */
    private String name;
    /** 菜单链接 */
    private String url;
    /** 子菜单列表 */
    private List<MenuVO> childList;
}

/** 构建菜单树函数 */
public static List<MenuVO> buildMenuTree(List<MenuDO> menuList) {
    // 检查列表为空
    if (CollectionUtils.isEmpty(menuList)) {
        return Collections.emptyList();
    }

    // 依次处理菜单
    int menuSize = menuList.size();
    List<MenuVO> rootList = new ArrayList<>(menuSize);
    Map<Long, MenuVO> menuMap = new HashMap<>(menuSize);
    for (MenuDO menuDO : menuList) {
        // 赋值菜单对象
        Long menuId = menuDO.getId();
        MenuVO menu = menuMap.get(menuId);
        if (Objects.isNull(menu)) {
            menu = new MenuVO();
            menu.setChildList(new ArrayList<>());
            menuMap.put(menuId, menu);
        }
        menu.setId(menuDO.getId());
        menu.setName(menuDO.getName());
        menu.setUrl(menuDO.getUrl());

        // 根据父标识处理
        Long parentId = menuDO.getParentId();
        if (Objects.nonNull(parentId)) {
            // 构建父菜单对象
            MenuVO parentMenu = menuMap.get(parentId);
            if (Objects.isNull(parentMenu)) {
                parentMenu = new MenuVO();
                parentMenu.setId(parentId);
                parentMenu.setChildList(new ArrayList<>());
                menuMap.put(parentId, parentMenu);
            }

            // 添加子菜单对象
            parentMenu.getChildList().add(menu);
        } else {
            // 添加根菜单对象
            rootList.add(menu);
        }
    }

    // 返回根菜单列表
    return rootList;
}
```
