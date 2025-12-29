// package com.example.demo.repository;
// import com.example.demo.model.MicroLesson;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import java.util.List;

// public interface MicroLessonRepository extends JpaRepository<MicroLesson, Long> {
//     @Query("SELECT m FROM MicroLesson m WHERE (:tags IS NULL OR m.tags LIKE %:tags%) " +
//            "AND (:diff IS NULL OR m.difficulty = :diff) " +
//            "AND (:ct IS NULL OR m.contentType = :ct)")
//     List<MicroLesson> findByFilters(@Param("tags") String tags, 
//                                    @Param("diff") String diff, 
//                                    @Param("ct") String ct);
// }
package com.example.demo.repository;

import com.example.demo.model.MicroLesson;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MicroLessonRepository extends JpaRepository<MicroLesson, Long> {

    @Query("SELECT m FROM MicroLesson m WHERE (:t IS NULL OR m.tags LIKE %:t%) " +
           "AND (:d IS NULL OR m.difficulty = :d) " +
           "AND (:c IS NULL OR m.contentType = :c)")
    List<MicroLesson> findByFilters(@Param("t") String tags,
                                    @Param("d") String difficulty,
                                    @Param("c") String contentType);
}