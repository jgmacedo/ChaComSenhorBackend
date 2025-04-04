package joao.ChaComOSenhor.domain.bible_verse;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Data
    @Table(name = "bible_verses")
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity
    public class BibleVerse {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;

        @Column(length = 1000)
        private String text;

        @Column(length = 100)
        private String reference;

        // This constructor can be used for testing
        public BibleVerse(String reference, String text) {
            this.reference = reference;
            this.text = text;
        }
    }