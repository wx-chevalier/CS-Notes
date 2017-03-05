package wx.toolkits.ds;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by apple on 16/9/6.
 */

/**
 * @function 测试Java是否PassByValue
 */
public class PassByValue {


    public void modifyArrayList(ArrayList arrayList, ArrayList arrayList2) {

        arrayList.add(2);

        arrayList2 = new ArrayList();

        arrayList2.add(2);

    }

    @Test
    public void test_modifyArrayList() {

        final ArrayList arrayList = new ArrayList();

        final ArrayList arrayList2 = new ArrayList();

        arrayList.add(1);

        arrayList2.add(2);

        modifyArrayList(arrayList, arrayList2);

        System.out.println(arrayList);

        System.out.println(arrayList2);

    }

}
