import com.kxhy.component.FilenameParseComponent;
import com.kxhy.domain.dto.FilenameParseResult;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Testa.class)
//@RequiredArgsConstructor
public class Testa {

//    @Autowired
//    private FilenameParseComponent filenameParseComponent;

    @Test
//    @ParameterizedTest
    void Test01() {

        FilenameParseComponent component = new FilenameParseComponent();
        FilenameParseResult result = component.parseFilename("斗破苍穹.txt");
        System.out.println(result);
    }

}
