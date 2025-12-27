public interface ProgressRepository extends JpaRepository<Progress, Long> {
    Optional<Progress> findByUserIdAndMicroLessonId(Long userId, Long lessonId);
    List<Progress> findByUserIdOrderByLastAccessedAtDesc(Long userId);
}
