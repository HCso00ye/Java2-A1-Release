import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class OnlineCoursesAnalyzer {

    public List<Course> courses = new ArrayList<>();

    public OnlineCoursesAnalyzer(String datasetPath) {
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(datasetPath, StandardCharsets.UTF_8));
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] info = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
                Course course = new Course(info[0], info[1], new Date(info[2]), info[3], info[4], info[5],
                        Integer.parseInt(info[6]), Integer.parseInt(info[7]),
                        Integer.parseInt(info[8]), Integer.parseInt(info[9]),
                        Integer.parseInt(info[10]), Double.parseDouble(info[11]),
                        Double.parseDouble(info[12]), Double.parseDouble(info[13]),
                        Double.parseDouble(info[14]), Double.parseDouble(info[15]),
                        Double.parseDouble(info[16]), Double.parseDouble(info[17]),
                        Double.parseDouble(info[18]), Double.parseDouble(info[19]),
                        Double.parseDouble(info[20]), Double.parseDouble(info[21]),
                        Double.parseDouble(info[22]));
                courses.add(course);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //1
    public Map<String, Integer> getPtcpCountByInst() {
        Map<String, Integer> ptcpCountByInst = new TreeMap<>();
        for (Course course : courses) {
            String institution = course.institution;
            int participants = course.participants;
            if (ptcpCountByInst.containsKey(institution)) {
                ptcpCountByInst.put(institution, ptcpCountByInst.get(institution) + participants);
            } else {
                ptcpCountByInst.put(institution, participants);
            }
        }
        return ptcpCountByInst;
    }

    //2
    //public Map<String, Integer> getPtcpCountByInstAndSubject() {return null;}
    public Map<String, Integer> getPtcpCountByInstAndSubject() {
        Map<String, Integer> ptcpCountByInstAndSubject = new TreeMap<>();
        for (Course course : courses) {
            String institution = course.institution;
            String subject = course.subject;
            String instAndSubj = institution + "-" + subject;
            int participants = course.participants;
            if (ptcpCountByInstAndSubject.containsKey(instAndSubj)) {
                ptcpCountByInstAndSubject.put
                        (instAndSubj, ptcpCountByInstAndSubject.get(instAndSubj) + participants);
            } else {
                ptcpCountByInstAndSubject.put(instAndSubj, participants);
            }
        }
        Map<String, Integer> sortedMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int count1 = ptcpCountByInstAndSubject.get(o1);
                int count2 = ptcpCountByInstAndSubject.get(o2);
                if (count1 != count2) {
                    return count2 - count1;
                } else {
                    return o1.compareTo(o2);
                }
            }
        });
        sortedMap.putAll(ptcpCountByInstAndSubject);
        return sortedMap;
    }

    //3
    //public Map<String, List<List<String>>> getCourseListOfInstructor() {return null;}

    public Map<String, List<List<String>>> getCourseListOfInstructor() {
        Map<String, List<List<String>>> result = new TreeMap<>();
        for (Course course : courses) {
            String[] instructors = course.instructors.split(", ");
            for (String instructor : instructors) {
                List<List<String>> courseList = result.getOrDefault(instructor, new ArrayList<>());
                if (instructors.length == 1) {
                    List<String> independentList;
                    if (courseList.size() == 0) {
                        courseList.add(new ArrayList<>());
                        courseList.add(new ArrayList<>());
                    }
                    independentList = courseList.get(0);
                    boolean isListed = false;
                    for (String curName : independentList) {
                        if (Objects.equals(curName, course.title)) {
                            isListed = true;
                            break;
                        }
                    }
                    if (!isListed) {
                        independentList.add(course.title);
                        courseList.set(0, independentList);
                    }
                } else {
                    List<String> coDevelopedList;
                    if (courseList.size() == 0) {
                        courseList.add(new ArrayList<>());
                        courseList.add(new ArrayList<>());
                    }
                    coDevelopedList = courseList.get(1);
                    boolean isListed = false;
                    for (String curName : coDevelopedList) {
                        if (Objects.equals(curName, course.title)) {
                            isListed = true;
                            break;
                        }
                    }
                    if (!isListed) {
                        coDevelopedList.add(course.title);
                        courseList.set(1, coDevelopedList);
                    }
                }
                result.put(instructor, courseList);
            }
        }
        for (Map.Entry<String, List<List<String>>> entry : result.entrySet()) {
            List<List<String>> courseList = entry.getValue();
            if (courseList.size() == 1) {
                courseList.add(new ArrayList<>());
            }
            for (List<String> list : courseList) {
                Collections.sort(list);
            }
        }
        return result;
    }


    //4
    //public List<String> getCourses(int topK, String by) {return null;}
    public List<String> getCourses(int topK, String by) {
        Map<String, Double[]> courseData = new HashMap<>();
        for (Course course : courses) {
            String title = course.title;
            // exist
            if (courseData.containsKey(title)) {
                Double[] data = courseData.get(title);
                data[0] = Math.max(course.totalHours, data[0]);
                data[1] = Math.max(course.participants, data[1]);
                courseData.put(title, data);
            } else {
                Double[] data = new Double[2];
                data[0] = course.totalHours;
                data[1] = (double) course.participants;
                courseData.put(title, data);
            }
        }
        // rank
        List<Map.Entry<String, Double[]>> sortedEntries = new ArrayList<>(courseData.entrySet());
        if (by.equals("hours")) {
            sortedEntries.sort((e1, e2) -> {
                double hours1 = e1.getValue()[0];
                double hours2 = e2.getValue()[0];
                if (hours1 != hours2) {
                    return Double.compare(hours2, hours1);
                } else {
                    return e1.getKey().compareTo(e2.getKey());
                }
            });
        } else if (by.equals("participants")) {
            sortedEntries.sort((e1, e2) -> {
                double participants1 = e1.getValue()[1];
                double participants2 = e2.getValue()[1];
                if (participants1 != participants2) {
                    return Double.compare(participants2, participants1);
                } else {
                    return e1.getKey().compareTo(e2.getKey());
                }
            });
        } else {
            throw new IllegalArgumentException("Invalid argument for by: " + by);
        }
        // get first kth
        List<String> topCourses = new ArrayList<>();
        Set<String> addedTitles = new HashSet<>();
        for (Map.Entry<String, Double[]> entry : sortedEntries) {
            String title = entry.getKey();
            if (!addedTitles.contains(title)) {
                topCourses.add(title);
                addedTitles.add(title);
                if (topCourses.size() >= topK) {
                    break;
                }
            }
        }
        return topCourses;
    }

    //5
    //public List<String> searchCourses(String courseSubject, double percentAudited, double totalCourseHours) {return null;}
    public List<String> searchCourses(String courseSubject, double percentAudited, double totalCourseHours) {
        List<String> matchingCourses = new ArrayList<>();
        for (Course course : courses) {
            if (course.subject.toLowerCase().contains(courseSubject.toLowerCase())) {
                if (course.percentAudited >= percentAudited) {
                    if (course.totalHours <= totalCourseHours) {
                        String title = course.title;
                        if (!matchingCourses.contains(title)) {
                            matchingCourses.add(title);
                        }
                    }
                }
            }
        }
        Collections.sort(matchingCourses);
        return matchingCourses;
    }

    //6
    //public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {return null;}
    public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {
        // calculate average Median Age, % Male, and % Bachelor's Degree or Higher for each course
        Map<String, CourseStats> courseStatsMap = new HashMap<>();
        for (Object obj : courses) {
            Course course = (Course) obj;
            String courseNumber = course.number;
            CourseStats courseStats = courseStatsMap.get(courseNumber);
            if (courseStats == null) {
                courseStats = new CourseStats(course);
                courseStatsMap.put(courseNumber, courseStats);
            }
            courseStats.addCourse(course);
        }
        // calculate similarity between user and each course's participants
        List<CourseSimilarity> courseSimilarities = new ArrayList<>();
        for (CourseStats courseStats : courseStatsMap.values()) {
            double avgMedianAge = courseStats.getAvgMedianAge();
            double avgPercentMale = courseStats.getAvgPercentMale();
            double avgPercentDegree = courseStats.getAvgPercentDegree();
            double similarity = Math.pow(age - avgMedianAge, 2)
                    + Math.pow(gender * 100 - avgPercentMale, 2)
                    + Math.pow(isBachelorOrHigher * 100 - avgPercentDegree, 2);
            courseSimilarities.add(new CourseSimilarity(courseStats.getLatestCourse(), similarity));
        }
        // sort courses by similarity and launch date
        Collections.sort(courseSimilarities);
        // return top 10 courses with smallest similarity value and latest launch date, without same title
        List<String> recommendedCourses = new ArrayList<>();
        Set<String> courseTitles = new HashSet<>();
        Set<String> courseNumbers = new HashSet<>();
        int count = 0;
        for (CourseSimilarity courseSimilarity : courseSimilarities) {
            Course course = courseSimilarity.getCourse();
            String number = course.number;
            String title = course.title;
            if (!courseTitles.contains(title) && !courseNumbers.contains(number)) {
                recommendedCourses.add(title);
                courseTitles.add(title);
                courseNumbers.add(number);
                count++;
                if (count == 10) {
                    break;
                }
            }
        }
        // sort recommended courses by alphabetical order of their titles if only two courses have the same similarity value
        if (recommendedCourses.size() >= 2 && courseSimilarities.get(9).getSimilarity() == courseSimilarities.get(10).getSimilarity()) {
            Collections.sort(recommendedCourses);
        }
        return recommendedCourses;
    }


    //store course statistics
    class CourseStats {
        private int count;
        private double totalMedianAge;
        private double totalPercentMale;
        private double totalPercentDegree;
        private Course latestCourse;

        public CourseStats(Course course) {
            this.latestCourse = course;
        }

        public void addCourse(Course course) {
            count++;
            totalMedianAge += course.getMedianAge();
            totalPercentMale += course.percentMale;
            totalPercentDegree += course.percentDegree;
            if (course.getLaunchDate().after(latestCourse.getLaunchDate())) {
                latestCourse = course;
            }
        }

        public double getAvgMedianAge() {
            return totalMedianAge / count;
        }

        public double getAvgPercentMale() {
            return totalPercentMale / count;
        }

        public double getAvgPercentDegree() {
            return totalPercentDegree / count;
        }

        public int getCount() {
            return count;
        }

        public Course getLatestCourse() {
            return latestCourse;
        }
    }

    // helper class to store course similarity values
    class CourseSimilarity implements Comparable<CourseSimilarity> {
        private Course course;
        private double similarity;

        public CourseSimilarity(Course course, double similarity) {
            this.course = course;
            this.similarity = similarity;
        }

        public Course getCourse() {
            return course;
        }

        public double getSimilarity() {
            return similarity;
        }

        @Override
        public int compareTo(CourseSimilarity other) {
            if (similarity == other.similarity) {
                // if similarity values are equal, sort by title
                return course.title.compareTo(other.course.title);
            } else {
                // sort by similarity value
                return Double.compare(similarity, other.similarity);
            }
        }
    }
}

class Course {
    String institution;
    String number;
    Date launchDate;
    String title;
    String instructors;
    String subject;
    int year;
    int honorCode;
    int participants;
    int audited;
    int certified;
    double percentAudited;
    double percentCertified;
    double percentCertified50;
    double percentVideo;
    double percentForum;
    double gradeHigherZero;
    double totalHours;
    double medianHoursCertification;
    double medianAge;
    double percentMale;
    double percentFemale;
    double percentDegree;

    public Course(String institution, String number, Date launchDate,
                  String title, String instructors, String subject,
                  int year, int honorCode, int participants,
                  int audited, int certified, double percentAudited,
                  double percentCertified, double percentCertified50,
                  double percentVideo, double percentForum, double gradeHigherZero,
                  double totalHours, double medianHoursCertification,
                  double medianAge, double percentMale, double percentFemale,
                  double percentDegree) {
        this.institution = institution;
        this.number = number;
        this.launchDate = launchDate;
        if (title.startsWith("\"")) title = title.substring(1);
        if (title.endsWith("\"")) title = title.substring(0, title.length() - 1);
        this.title = title;
        if (instructors.startsWith("\"")) instructors = instructors.substring(1);
        if (instructors.endsWith("\"")) instructors = instructors.substring(0, instructors.length() - 1);
        this.instructors = instructors;
        if (subject.startsWith("\"")) subject = subject.substring(1);
        if (subject.endsWith("\"")) subject = subject.substring(0, subject.length() - 1);
        this.subject = subject;
        this.year = year;
        this.honorCode = honorCode;
        this.participants = participants;
        this.audited = audited;
        this.certified = certified;
        this.percentAudited = percentAudited;
        this.percentCertified = percentCertified;
        this.percentCertified50 = percentCertified50;
        this.percentVideo = percentVideo;
        this.percentForum = percentForum;
        this.gradeHigherZero = gradeHigherZero;
        this.totalHours = totalHours;
        this.medianHoursCertification = medianHoursCertification;
        this.medianAge = medianAge;
        this.percentMale = percentMale;
        this.percentFemale = percentFemale;
        this.percentDegree = percentDegree;
    }
    public double getMedianAge() {
        return medianAge;
    }
    public Date getLaunchDate() {
        return launchDate;
    }

}
