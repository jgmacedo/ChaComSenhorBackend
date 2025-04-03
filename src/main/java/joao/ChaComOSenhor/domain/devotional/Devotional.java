package joao.ChaComOSenhor.domain.devotional;

import jakarta.persistence.*;
import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    private String content;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "bible_verses_id")
    private BibleVerse bibleVerse;
}
