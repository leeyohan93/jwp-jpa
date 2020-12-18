package jpa.domain;

import jpa.domain.Line.Color;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;


@DataJpaTest
class LineRepositoryTest {

    @Autowired
    private LineRepository lines;

    @Autowired
    private TestEntityManager em;

    @DisplayName("단건 조회를 확인합니다.")
    @Test
    void findOne() {
        // given
        String lineName = "1호선";
        Line expected = lines.save(new Line(lineName, Color.BLUE));

        // when
        Line actual = lines.findByName(lineName).get();

        // then
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual).isEqualTo(expected)
        );
    }

    @DisplayName("조회 시 값이 없을 경우 null 이 반환됩니다.")
    @Test
    void findOneNull() {
        // when
        Optional<Line> lineOptional = lines.findByName("20호선");

        // then
        assertThat(lineOptional.isPresent()).isFalse();
    }

    @DisplayName("여러건 조회를 확인합니다.")
    @Test
    void findAll() {
        // given
        Line line1 = new Line("1호선", Color.BLUE);
        Line line2 = new Line("2호선", Color.GREEN);
        Line line3 = new Line("3호선", Color.RED);
        lines.saveAll(Arrays.asList(line1, line2, line3));

        // when
        List<Line> lineList = lines.findAll();

        // then
        assertAll(
                () -> assertThat(lineList).isNotNull(),
                () -> assertThat(lineList).containsExactly(line1, line2, line3)
        );
    }

    @DisplayName("정상적으로 저장되는지 확인합니다.")
    @Test
    void save() {
        // given
        Line expected = new Line("1호선", Color.BLUE);

        // when
        Line actual = lines.save(expected);

        // then
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getCreatedDate()).isNotNull(),
                () -> assertThat(actual).isEqualTo(expected),
                () -> assertThat(actual).isSameAs(expected)
        );
    }

    @DisplayName("변경 감지가 정상적으로 작동하는지 확인합니다.")
    @Test
    void update() {
        // given
        Line savedLine = lines.save(new Line("1호선", Color.BLUE));

        // when
        Line expected = savedLine.changeName("2호선");
        Line actual = lines.findByName(expected.getName()).get();

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getModifiedDate()).isNotNull(),
                () -> assertThat(actual).isEqualTo(expected)
        );
    }

    @DisplayName("Line.name 의 unique 속성을 확인합니다.")
    @Test
    void unique() {
        // given
        String lineName = "1호선";
        lines.save(new Line(lineName, Color.BLUE));

        // when / then
        assertThrows(DataIntegrityViolationException.class, () -> lines.save(new Line(lineName, Color.RED)));
    }

    @DisplayName("노선 조회 시 속한 지하철역을 볼 수 있다.")
    @Test
    void checkStations() {
        // given
        // 지하철 노선 저장
        String lineName = "2호선";
        Line line1 = new Line(lineName, Color.GREEN);
        em.persist(line1);

        // 지하철역 저장
        Station gangnam = new Station("강남역");
        Station hapjeong = new Station("합정역");
        em.persist(gangnam);
        em.persist(hapjeong);

        // 지하철역 노선 저장
        em.persist(new LineStation(gangnam, line1));
        em.persist(new LineStation(hapjeong, line1));

        // when
        Line line = lines.findByName(lineName).get();
        List<Station> stations = line.stations();

        // then
        assertAll(
                () -> assertThat(stations).isNotNull(),
                () -> assertThat(stations).contains(gangnam, hapjeong)
        );
    }
}
