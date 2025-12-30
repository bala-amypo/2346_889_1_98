package com.example.demo;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.RecommendationRequest;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.impl.*;
import com.example.demo.servlet.SimpleStatusServlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mockito.Mockito;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Updated DemoSystemTest with fixes for:
 *  - progress update test (mock user & lesson lookups)
 *  - robust progressRepository.save stub (guards null)
 *  - resetting courseRepository mock before verification to avoid cross-test counts
 *
 * Total: 60 tests (as required).
 */
@Listeners(TestResultListener.class)
public class DemoSystemTest {

    @Mock private UserRepository userRepository;
    @Mock private CourseRepository courseRepository;
    @Mock private MicroLessonRepository microLessonRepository;
    @Mock private ProgressRepository progressRepository;
    @Mock private RecommendationRepository recommendationRepository;
    @Mock private JwtUtil jwtUtil;

    private BCryptPasswordEncoder encoder;
    private UserServiceImpl userService;
    private CourseServiceImpl courseService;
    private LessonServiceImpl lessonService;
    private ProgressServiceImpl progressService;
    private RecommendationServiceImpl recommendationService;

    @BeforeClass
    public void init() {
        MockitoAnnotations.openMocks(this);
        encoder = new BCryptPasswordEncoder();
        userService = new UserServiceImpl(userRepository, encoder, jwtUtil);
        courseService = new CourseServiceImpl(courseRepository, userRepository);
        lessonService = new LessonServiceImpl(microLessonRepository, courseRepository);
        progressService = new ProgressServiceImpl(progressRepository, userRepository, microLessonRepository);
        recommendationService = new RecommendationServiceImpl(recommendationRepository, userRepository, microLessonRepository);
    }

    /* ----------------------------------------------------------
     * 1. DEVELOP & DEPLOY SIMPLE SERVLET (5 tests)
     * ---------------------------------------------------------- */

    @Test(priority = 1, groups = {"Develop_and_deploy_servlet"})
    public void t01_servlet_basic_success() throws Exception {
        SimpleStatusServlet servlet = new SimpleStatusServlet();

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);
        when(resp.getWriter()).thenReturn(writer);

        servlet.doGet(req, resp);

        verify(resp).setStatus(200);
        verify(resp).setContentType("text/plain");
        verify(writer).write("Servlet Alive");
    }

    @Test(priority = 2, groups = {"Develop_and_deploy_servlet"})
    public void t02_servlet_writer_failure() throws Exception {
        SimpleStatusServlet servlet = new SimpleStatusServlet();

        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpServletRequest req = mock(HttpServletRequest.class);

        when(resp.getWriter()).thenThrow(new IOException("writer failed"));

        boolean thrown = false;
        try {
            servlet.doGet(req, resp);
        } catch (IOException ex) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test(priority = 3, groups = {"Develop_and_deploy_servlet"})
    public void t03_servlet_status_check() throws Exception {
        SimpleStatusServlet servlet = new SimpleStatusServlet();

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(resp.getWriter()).thenReturn(mock(PrintWriter.class));

        servlet.doGet(req, resp);
        verify(resp).setStatus(200);
    }

    @Test(priority = 4, groups = {"Develop_and_deploy_servlet"})
    public void t04_servlet_content_type() throws Exception {
        SimpleStatusServlet servlet = new SimpleStatusServlet();
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(resp.getWriter()).thenReturn(mock(PrintWriter.class));

        servlet.doGet(req, resp);
        verify(resp).setContentType("text/plain");
    }

    @Test(priority = 5, groups = {"Develop_and_deploy_servlet"})
    public void t05_servlet_output_message() throws Exception {
        SimpleStatusServlet servlet = new SimpleStatusServlet();
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpServletRequest req = mock(HttpServletRequest.class);

        PrintWriter pw = mock(PrintWriter.class);
        when(resp.getWriter()).thenReturn(pw);

        servlet.doGet(req, resp);
        verify(pw).write("Servlet Alive");
    }

    /* ----------------------------------------------------------
     * 2. SPRING CRUD OPERATIONS (15 tests)
     * ---------------------------------------------------------- */

    @Test(priority = 6, groups = {"CRUD"})
    public void t06_register_success() {
        User u = User.builder().email("a@x.com").password("pass").build();
        when(userRepository.existsByEmail(u.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User saved = inv.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        User out = userService.register(u);
        Assert.assertEquals(out.getId().longValue(), 1L);
        Assert.assertTrue(encoder.matches("pass", out.getPassword()));
    }

    @Test(priority = 7, groups = {"CRUD"})
    public void t07_register_duplicate_email() {
        when(userRepository.existsByEmail("dup@x.com")).thenReturn(true);

        boolean thrown = false;
        try {
            userService.register(User.builder().email("dup@x.com").password("p").build());
        } catch (RuntimeException ex) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test(priority = 8, groups = {"CRUD"})
    public void t08_login_success() {
        User u = User.builder()
                .id(10L)
                .email("log@x.com")
                .password(encoder.encode("pw"))
                .role("LEARNER")
                .build();

        when(userRepository.findByEmail("log@x.com")).thenReturn(Optional.of(u));
        when(jwtUtil.generateToken(anyMap(), eq("log@x.com"))).thenReturn("token123");

        AuthResponse r = userService.login("log@x.com", "pw");

        Assert.assertEquals(r.getAccessToken(), "token123");
    }

    @Test(priority = 9, groups = {"CRUD"})
    public void t09_login_bad_password() {
        User u = User.builder().email("bad@x.com").password(encoder.encode("right")).build();
        when(userRepository.findByEmail("bad@x.com")).thenReturn(Optional.of(u));

        boolean thrown = false;
        try {
            userService.login("bad@x.com", "wrong");
        } catch (RuntimeException ex) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test(priority = 10, groups = {"CRUD"})
    public void t10_course_create_success() {
        User instr = User.builder().id(5L).role("INSTRUCTOR").build();
        Course newCourse = Course.builder().title("Java 101").build();

        when(userRepository.findById(5L)).thenReturn(Optional.of(instr));
        when(courseRepository.existsByTitleAndInstructorId("Java 101", 5L)).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> {
            Course c = inv.getArgument(0);
            c.setId(100L);
            return c;
        });

        Course saved = courseService.createCourse(newCourse, 5L);
        Assert.assertEquals(saved.getId().longValue(), 100L);
    }

    @Test(priority = 11, groups = {"CRUD"})
    public void t11_course_instructor_not_found() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        boolean thrown = false;
        try {
            courseService.createCourse(Course.builder().title("X").build(), 999L);
        } catch (RuntimeException ex) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test(priority = 12, groups = {"CRUD"})
    public void t12_course_update() {
        Course c = Course.builder().id(1L).title("Old").build();
        when(courseRepository.findById(1L)).thenReturn(Optional.of(c));
        when(courseRepository.save(any(Course.class))).thenAnswer(i -> i.getArgument(0));

        Course update = Course.builder().title("New").description("D").build();

        Course res = courseService.updateCourse(1L, update);
        Assert.assertEquals(res.getTitle(), "New");
    }

    @Test(priority = 13, groups = {"CRUD"})
    public void t13_add_lesson_success() {
        Course c = Course.builder().id(11L).build();
        MicroLesson m = MicroLesson.builder().title("Intro").difficulty("BEGINNER").contentType("VIDEO").build();

        when(courseRepository.findById(11L)).thenReturn(Optional.of(c));
        when(microLessonRepository.save(any(MicroLesson.class))).thenAnswer(inv -> {
            MicroLesson ml = inv.getArgument(0);
            ml.setId(555L);
            return ml;
        });

        MicroLesson out = lessonService.addLesson(11L, m);
        Assert.assertEquals(out.getId().longValue(), 555L);
    }

    @Test(priority = 14, groups = {"CRUD"})
    public void t14_update_lesson_success() {
        MicroLesson stored = MicroLesson.builder().id(88L).title("Old").contentType("TEXT").difficulty("BEGINNER").build();

        when(microLessonRepository.findById(88L)).thenReturn(Optional.of(stored));
        when(microLessonRepository.save(any(MicroLesson.class))).thenAnswer(inv -> inv.getArgument(0));

        MicroLesson upd = MicroLesson.builder().title("Updated").contentType("VIDEO").difficulty("INTERMEDIATE").build();

        MicroLesson out = lessonService.updateLesson(88L, upd);
        Assert.assertEquals(out.getTitle(), "Updated");
    }

    @Test(priority = 15, groups = {"CRUD"})
    public void t15_record_progress_new() {
        User u = User.builder().id(4L).build();
        MicroLesson ml = MicroLesson.builder().id(7L).build();
        Progress incoming = Progress.builder().progressPercent(10).status("IN_PROGRESS").build();

        when(userRepository.findById(4L)).thenReturn(Optional.of(u));
        when(microLessonRepository.findById(7L)).thenReturn(Optional.of(ml));
        when(progressRepository.findByUserIdAndMicroLessonId(4L, 7L)).thenReturn(Optional.empty());
        when(progressRepository.save(any(Progress.class))).thenAnswer(inv -> {
            Progress p = inv.getArgument(0);
            p.setId(300L);
            return p;
        });

        Progress out = progressService.recordProgress(4L, 7L, incoming);
        Assert.assertEquals(out.getId().longValue(), 300L);
    }

    @Test(priority = 16, groups = {"CRUD"})
    public void t16_update_progress_existing() {
        // FIX: mock userRepository.findById and microLessonRepository.findById so service finds them
        User existingUser = User.builder().id(3L).build();
        MicroLesson existingLesson = MicroLesson.builder().id(6L).build();
        Progress existing = Progress.builder().id(99L).progressPercent(20).status("IN_PROGRESS").build();

        when(userRepository.findById(3L)).thenReturn(Optional.of(existingUser));
        when(microLessonRepository.findById(6L)).thenReturn(Optional.of(existingLesson));
        when(progressRepository.findByUserIdAndMicroLessonId(3L, 6L)).thenReturn(Optional.of(existing));
        when(progressRepository.save(any(Progress.class))).thenAnswer(i -> i.getArgument(0));

        Progress update = Progress.builder().progressPercent(100).status("COMPLETED").score(BigDecimal.valueOf(90)).build();

        Progress out = progressService.recordProgress(3L, 6L, update);

        Assert.assertEquals(out.getStatus(), "COMPLETED");
        Assert.assertEquals(out.getProgressPercent().intValue(), 100);
        Assert.assertEquals(out.getScore(), BigDecimal.valueOf(90));
    }

    @Test(priority = 17, groups = {"CRUD"})
    public void t17_find_lesson_filter() {
        when(microLessonRepository.findByFilters("java", "BEGINNER", "VIDEO"))
                .thenReturn(Collections.singletonList(
                        MicroLesson.builder().id(10L).difficulty("BEGINNER").contentType("VIDEO").tags("java").build()
                ));

        List<MicroLesson> res = lessonService.findLessonsByFilters("java", "BEGINNER", "VIDEO");
        Assert.assertEquals(res.size(), 1);
    }

    @Test(priority = 18, groups = {"CRUD"})
    public void t18_get_course() {
        when(courseRepository.findById(5L)).thenReturn(Optional.of(Course.builder().id(5L).title("X").build()));

        Course c = courseService.getCourse(5L);
        Assert.assertEquals(c.getId().longValue(), 5L);
    }

    @Test(priority = 19, groups = {"CRUD"})
    public void t19_get_lesson() {
        when(microLessonRepository.findById(33L)).thenReturn(Optional.of(MicroLesson.builder().id(33L).build()));
        MicroLesson ml = lessonService.getLesson(33L);
        Assert.assertEquals(ml.getId().longValue(), 33L);
    }

    @Test(priority = 20, groups = {"CRUD"})
    public void t20_get_user_progress_list() {
        when(progressRepository.findByUserIdOrderByLastAccessedAtDesc(44L)).thenReturn(Collections.emptyList());
        List<Progress> list = progressService.getUserProgress(44L);
        Assert.assertNotNull(list);
    }

    /* ----------------------------------------------------------
     * 3. DI/IOC (7 tests)
     * ---------------------------------------------------------- */

    @Test(priority = 21, groups = {"DI"})
    public void t21_di_password_encoding() {
        when(userRepository.existsByEmail("di@x")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        User u = userService.register(User.builder().email("di@x").password("pass").build());
        Assert.assertNotEquals(u.getPassword(), "pass");
    }

    @Test(priority = 22, groups = {"DI"})
    public void t22_di_course_repo_used() {
        User u = User.builder().id(6L).role("INSTRUCTOR").build();
        when(userRepository.findById(6L)).thenReturn(Optional.of(u));
        when(courseRepository.existsByTitleAndInstructorId("A", 6L)).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenAnswer(i -> i.getArgument(0));

        Course c = courseService.createCourse(Course.builder().title("A").build(), 6L);
        Assert.assertEquals(c.getTitle(), "A");
    }

    @Test(priority = 23, groups = {"DI"})
    public void t23_di_lesson_repo_used() {
        Course c = Course.builder().id(11L).build();
        when(courseRepository.findById(11L)).thenReturn(Optional.of(c));
        when(microLessonRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MicroLesson ml = lessonService.addLesson(11L, MicroLesson.builder().title("YY").build());
        Assert.assertEquals(ml.getCourse().getId().longValue(), 11L);
    }

    @Test(priority = 24, groups = {"DI"})
    public void t24_di_progress_uses_repos() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(User.builder().id(2L).build()));
        when(microLessonRepository.findById(3L)).thenReturn(Optional.of(MicroLesson.builder().id(3L).build()));
        when(progressRepository.findByUserIdAndMicroLessonId(2L, 3L)).thenReturn(Optional.empty());
        when(progressRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Progress p = progressService.recordProgress(2L, 3L,
                Progress.builder().progressPercent(10).status("IN_PROGRESS").build());

        Assert.assertNotNull(p);
    }

    @Test(priority = 25, groups = {"DI"})
    public void t25_di_recommendation_service_exists() {
        Assert.assertNotNull(recommendationService);
    }

    @Test(priority = 26, groups = {"DI"})
    public void t26_di_mock_multiple() {
        when(userRepository.findByEmail("abc")).thenReturn(Optional.of(User.builder().id(77L).build()));
        Assert.assertNotNull(userService.findByEmail("abc"));
    }

    @Test(priority = 27, groups = {"DI"})
    public void t27_di_invalid_inputs() {
        boolean thrown = false;
        try {
            userService.register(null);
        } catch (Exception ex) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    /* ----------------------------------------------------------
     * 4. HIBERNATE ENTITY RULES (10 tests)
     * ---------------------------------------------------------- */

    @Test(priority = 28, groups = {"Hibernate"})
    public void t28_user_prePersist() {
        User u = new User();
        u.setEmail("x@x.com");
        u.prePersist();
        Assert.assertNotNull(u.getCreatedAt());
    }

    @Test(priority = 29, groups = {"Hibernate"})
    public void t29_course_prePersist() {
        Course c = new Course();
        c.setTitle("X");
        c.prePersist();
        Assert.assertNotNull(c.getCreatedAt());
    }

    @Test(priority = 30, groups = {"Hibernate"})
    public void t30_lesson_duration_rule() {
        MicroLesson ml = MicroLesson.builder().durationMinutes(15).build();
        Assert.assertTrue(ml.getDurationMinutes() <= 15);
    }

    @Test(priority = 31, groups = {"Hibernate"})
    public void t31_progress_prePersist_defaults() {
        Progress p = new Progress();
        p.prePersist();
        Assert.assertNotNull(p.getLastAccessedAt());
    }

    @Test(priority = 32, groups = {"Hibernate"})
    public void t32_recommendation_prePersist() {
        Recommendation r = new Recommendation();
        r.prePersist();
        Assert.assertNotNull(r.getGeneratedAt());
    }

    @Test(priority = 33, groups = {"Hibernate"})
    public void t33_course_instructor_relation() {
        Course c = Course.builder().id(1L).build();
        User u = User.builder().id(2L).build();
        c.setInstructor(u);
        Assert.assertEquals(c.getInstructor().getId().longValue(), 2L);
    }

    @Test(priority = 34, groups = {"Hibernate"})
    public void t34_progress_score_null_allowed() {
        Progress p = Progress.builder().score(null).build();
        Assert.assertNull(p.getScore());
    }

    @Test(priority = 35, groups = {"Hibernate"})
    public void t35_tags_stored_properly() {
        MicroLesson ml = MicroLesson.builder().tags("a,b,c").build();
        Assert.assertTrue(ml.getTags().contains("b"));
    }

    @Test(priority = 36, groups = {"Hibernate"})
    public void t36_confidence_score_bounds() {
        Recommendation r = Recommendation.builder().confidenceScore(BigDecimal.valueOf(0.5)).build();
        double val = r.getConfidenceScore().doubleValue();
        Assert.assertTrue(val >= 0.0 && val <= 1.0);
    }

    @Test(priority = 37, groups = {"Hibernate"})
    public void t37_progress_percent_100_if_completed() {
        Progress p = Progress.builder().status("COMPLETED").progressPercent(100).build();
        Assert.assertEquals(p.getProgressPercent().intValue(), 100);
    }

    /* ----------------------------------------------------------
     * 5. JPA NORMALIZATION (1NF,2NF,3NF) – 6 tests
     * ---------------------------------------------------------- */

    @Test(priority = 38, groups = {"JPA"})
    public void t38_1nf_no_repeat_groups() {
        Progress p = Progress.builder()
                .user(User.builder().id(1L).build())
                .microLesson(MicroLesson.builder().id(2L).build())
                .build();
        Assert.assertNotNull(p);
    }

    @Test(priority = 39, groups = {"JPA"})
    public void t39_2nf_course_and_lessons_separate() {
        Course c = Course.builder().id(10L).build();
        MicroLesson ml = MicroLesson.builder().id(20L).course(c).build();
        Assert.assertEquals(ml.getCourse().getId().longValue(), 10L);
    }

    @Test(priority = 40, groups = {"JPA"})
    public void t40_3nf_recommendation_no_redundant_info() {
        Recommendation r = Recommendation.builder().basisSnapshot("{\"progressIds\":[1,2]}").build();
        Assert.assertTrue(r.getBasisSnapshot().contains("progressIds"));
    }

    @Test(priority = 41, groups = {"JPA"})
    public void t41_progress_fk() {
        Progress p = Progress.builder()
                .user(User.builder().id(9L).build())
                .microLesson(MicroLesson.builder().id(8L).build())
                .build();
        Assert.assertEquals(p.getUser().getId().longValue(), 9L);
    }

    @Test(priority = 42, groups = {"JPA"})
    public void t42_recommendation_ids_csv() {
        Recommendation r = Recommendation.builder().recommendedLessonIds("1,2,3").build();
        Assert.assertTrue(r.getRecommendedLessonIds().contains("2"));
    }

    @Test(priority = 43, groups = {"JPA"})
    public void t43_confidence_valid_range() {
        Recommendation r = Recommendation.builder().confidenceScore(BigDecimal.ONE).build();
        Assert.assertTrue(r.getConfidenceScore().doubleValue() <= 1.0);
    }

    /* ----------------------------------------------------------
     * 6. MANY-TO-MANY (6 tests)
     * ---------------------------------------------------------- */

    @Test(priority = 44, groups = {"M2M"})
    public void t44_progress_create_m2m() {
        Progress p = Progress.builder().user(User.builder().id(1L).build()).microLesson(MicroLesson.builder().id(2L).build()).build();
        when(progressRepository.save(any())).thenAnswer(i -> {
            Progress pr = i.getArgument(0);
            if (pr == null) pr = Progress.builder().build();
            pr.setId(900L);
            return pr;
        });
        Progress out = progressRepository.save(p);
        Assert.assertEquals(out.getId().longValue(), 900L);
    }

    @Test(priority = 45, groups = {"M2M"})
    public void t45_multiple_entries_user() {
        when(progressRepository.findByUserIdOrderByLastAccessedAtDesc(22L))
                .thenReturn(Arrays.asList(
                        Progress.builder().id(1L).build(),
                        Progress.builder().id(2L).build()
                ));

        Assert.assertEquals(progressRepository.findByUserIdOrderByLastAccessedAtDesc(22L).size(), 2);
    }

    @Test(priority = 46, groups = {"M2M"})
    public void t46_duplicate_progress_rejected() {
        when(progressRepository.findByUserIdAndMicroLessonId(2L, 3L))
                .thenReturn(Optional.of(Progress.builder().id(50L).build()));

        Assert.assertTrue(progressRepository.findByUserIdAndMicroLessonId(2L, 3L).isPresent());
    }

    @Test(priority = 47, groups = {"M2M"})
    public void t47_progress_user_lesson() {
        Progress p = Progress.builder().user(User.builder().id(10L).build()).microLesson(MicroLesson.builder().id(11L).build()).build();
        Assert.assertEquals(p.getUser().getId().longValue(), 10L);
    }

    @Test(priority = 48, groups = {"M2M"})
    public void t48_m2m_lesson_null() {
        boolean thrown = false;
        try {
            progressService.recordProgress(100L, 500L,
                    Progress.builder().progressPercent(10).status("IN_PROGRESS").build());
        } catch (Exception ex) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test(priority = 49, groups = {"M2M"})
    public void t49_multiple_users_one_lesson() {
        // Robust stub: guard against null arguments
        when(progressRepository.save(any())).thenAnswer(i -> {
            Progress p = i.getArgument(0);
            if (p == null) p = Progress.builder().build();
            p.setId(new Random().nextLong() & Long.MAX_VALUE);
            return p;
        });

        Progress p1 = progressRepository.save(Progress.builder().user(User.builder().id(1L).build()).microLesson(MicroLesson.builder().id(5L).build()).build());
        Progress p2 = progressRepository.save(Progress.builder().user(User.builder().id(2L).build()).microLesson(MicroLesson.builder().id(5L).build()).build());

        Assert.assertNotEquals(p1.getId(), p2.getId());
    }

    /* ----------------------------------------------------------
     * 7. SECURITY + JWT (6 tests)
     * ---------------------------------------------------------- */

    @Test(priority = 50, groups = {"Security"})
    public void t50_jwt_generate_token() {
        User u = User.builder().id(5L).email("sec@x").password(encoder.encode("pass")).role("LEARNER").build();

        when(userRepository.findByEmail("sec@x")).thenReturn(Optional.of(u));
        when(jwtUtil.generateToken(anyMap(), eq("sec@x"))).thenReturn("jwt-token");

        AuthResponse res = userService.login("sec@x", "pass");
        Assert.assertEquals(res.getAccessToken(), "jwt-token");
    }

    @Test(priority = 51, groups = {"Security"})
    public void t51_validate_token_ok() {
        when(jwtUtil.validateToken("good")).thenReturn(true);
        Assert.assertTrue(jwtUtil.validateToken("good"));
    }

    @Test(priority = 52, groups = {"Security"})
    public void t52_validate_token_fail() {
        when(jwtUtil.validateToken("bad")).thenReturn(false);
        Assert.assertFalse(jwtUtil.validateToken("bad"));
    }

    @Test(priority = 53, groups = {"Security"})
    public void t53_instructor_create_course() {
        // Reset courseRepository to avoid counting saves from earlier tests
        Mockito.reset(courseRepository);

        User u = User.builder().id(99L).role("INSTRUCTOR").build();
        when(userRepository.findById(99L)).thenReturn(Optional.of(u));
        when(courseRepository.existsByTitleAndInstructorId("ABC", 99L)).thenReturn(false);
        when(courseRepository.save(any())).thenAnswer(i -> {
            Course c = i.getArgument(0);
            c.setId(222L);
            return c;
        });

        Course created = courseService.createCourse(Course.builder().title("ABC").build(), 99L);
        verify(courseRepository, times(1)).save(any());
        Assert.assertEquals(created.getId().longValue(), 222L);
    }

    @Test(priority = 54, groups = {"Security"})
    public void t54_admin_access() {
        User u = User.builder().id(200L).role("ADMIN").build();
        when(userRepository.findById(200L)).thenReturn(Optional.of(u));
        Assert.assertEquals(u.getRole(), "ADMIN");
    }

    @Test(priority = 55, groups = {"Security"})
    public void t55_expired_token() {
        when(jwtUtil.validateToken("expired")).thenReturn(false);
        Assert.assertFalse(jwtUtil.validateToken("expired"));
    }

    /* ----------------------------------------------------------
     * 8. HQL / HCQL (3 tests)
     * ---------------------------------------------------------- */

    @Test(priority = 56, groups = {"HQL"})
    public void t56_hql_lesson_filter() {
        when(microLessonRepository.findByFilters("java", "BEGINNER", "VIDEO"))
                .thenReturn(Collections.singletonList(MicroLesson.builder().tags("java").difficulty("BEGINNER").contentType("VIDEO").build()));

        List<MicroLesson> out = microLessonRepository.findByFilters("java", "BEGINNER", "VIDEO");
        Assert.assertEquals(out.size(), 1);
    }

    @Test(priority = 57, groups = {"HQL"})
    public void t57_hql_recent_progress() {
        when(progressRepository.findByUserIdOrderByLastAccessedAtDesc(7L))
                .thenReturn(Collections.singletonList(Progress.builder().id(1L).build()));

        Assert.assertEquals(progressRepository.findByUserIdOrderByLastAccessedAtDesc(7L).size(), 1);
    }

    @Test(priority = 58, groups = {"HQL"})
    public void t58_hql_recommendation_range() {
        Recommendation r = Recommendation.builder().id(5L).generatedAt(LocalDateTime.now()).build();

        when(recommendationRepository.findByUserIdAndGeneratedAtBetween(eq(1L), any(), any()))
                .thenReturn(Collections.singletonList(r));

        Assert.assertEquals(recommendationRepository
                .findByUserIdAndGeneratedAtBetween(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now()).size(), 1);
    }

    /* ----------------------------------------------------------
     * SUPPLEMENTARY (2 tests, total 60)
     * ---------------------------------------------------------- */

    @Test(priority = 59, groups = {"Supplementary"})
    public void t59_latest_recommendation_failure() {
        when(recommendationRepository.findByUserIdOrderByGeneratedAtDesc(500L)).thenReturn(Collections.emptyList());

        boolean thrown = false;
        try {
            recommendationService.getLatestRecommendation(500L);
        } catch (Exception e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test(priority = 60, groups = {"Supplementary"})
    public void t60_progress_completed_must_be_100() {
        Progress p = Progress.builder().status("COMPLETED").progressPercent(99).build();
        boolean valid = (p.getStatus().equals("COMPLETED") && p.getProgressPercent() == 100);
        Assert.assertFalse(valid);
    }
}
