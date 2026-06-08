import com.kxhy.admin.AdminSystemApplication;
import com.kxhy.admin.domain.vo.AdminMenuVO;
import com.kxhy.admin.service.AdminMenuService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = AdminSystemApplication.class)
public class TestAdminMenu {

    @Resource
    private AdminMenuService adminMenuService;

    @Test
    void adminMenuTest() {
        List<AdminMenuVO> adminMenuVOS = adminMenuService.queryMenuListByAdminId(1L);
        System.out.println("adminMenuVOS = " + adminMenuVOS);
    }

}
