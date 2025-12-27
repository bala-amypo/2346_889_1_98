public interface MicroLessonRepository extends JpaRepository<MicroLesson, Long> {
    List<MicroLesson> findByTagsContainingAndDifficultyAndContentType(
        String tags, String difficulty, String contentType);
}
