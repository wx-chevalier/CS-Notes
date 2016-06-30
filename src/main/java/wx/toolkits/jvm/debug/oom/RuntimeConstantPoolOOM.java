package wx.toolkits.jvm.debug.oom;

/**
 * Created by apple on 16/6/24.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * @function 演示运行时常量池导致的内存溢出
 */
public class RuntimeConstantPoolOOM {

    public static void main(String args[]){
        // 使用List保持常量池引用,避免Full GC回收常量池行为
        List<String> list = new ArrayList<>();

        //10MB的PermSize在Integer范围内产生OOM
        int i = 0;

        while (true){
            list.add(String.valueOf(i++).intern());
        }

    }

}
