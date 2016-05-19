package wx.toolkits.basic.class_object.utils.lombok.data;

import lombok.*;

/**
 * Created by apple on 16/5/19.
 */
public class ConstructorExample {

    @AllArgsConstructor(staticName = "of")
    public static class AllArgsExample {
        private int x, y;
        private String str;
        @NonNull
        private String field;
    }

    @RequiredArgsConstructor(staticName = "of")
    public static class RequiredArgsExample {

        private int x, y;
        private String str;
        @NonNull
        private String field;

    }

    @NoArgsConstructor
    public static class NoArgsExample {
        @NonNull
        private String field;
    }

}
