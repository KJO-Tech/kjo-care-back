package kjo.care.msvc_blog.entities;

import jakarta.persistence.*;
import kjo.care.msvc_blog.enums.BlogState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "blog")
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "userId")
    private String userId;

    @ManyToOne
    @JoinColumn(name = "categoryId", referencedColumnName = "id")
    private Category category;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "video", nullable = true)
    private String video;

    @Column(name = "image", nullable = true)
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private BlogState state;

    @Column(name = "publishedDate")
    private LocalDate publishedDate = LocalDate.now();

    @Column(name = "modifiedDate")
    private LocalDate modifiedDate = LocalDate.now();

}
