import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class test {

    public static void main(String[] args) {
        OnlineCoursesAnalyzer coursesAnalyzer = new OnlineCoursesAnalyzer("resources/local.csv");

        //System.out.println(coursesAnalyzer.getCourses(10,"hours"));
        List<String> answer = coursesAnalyzer.recommendCourses(25,1,1);

        for (int i = 0; i < answer.size(); i++) {
            System.out.println(answer.get(i));
        }

        Date s1 = new Date("2021/2/2");
        Date s2 = new Date("2022/2/2");
        System.out.println(s1.after(s2));
    }
}
