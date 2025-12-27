public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByTitleAndInstructorId(String title, Long instructorId);
    List<Course> findByInstructorId(Long instructorId);
}
