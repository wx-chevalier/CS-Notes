package wx.toolkits.basic.class_object.utils.lombok.object;

import lombok.experimental.Builder;

import java.util.Set;

@Builder
public class BuilderExample {
    private String name;
    private int age;
    private Set<String> occupations;

    public static void main(String args[]) {

        BuilderExample builderExample = BuilderExample.builder().build();

    }

}