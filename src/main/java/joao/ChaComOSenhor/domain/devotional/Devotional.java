package joao.ChaComOSenhor.domain.devotional;

import jakarta.persistence.*;
import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Table(name = "devotionals")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Devotional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 3000)
    private String reflection;

    @Column(length = 500)
    private String prayer;

    @Column(length = 1000)
    private String practicalApplication;

    @Column(length = 1000)
    private String supportingVerses;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "bible_verses_id")
    private BibleVerse bibleVerse;
}