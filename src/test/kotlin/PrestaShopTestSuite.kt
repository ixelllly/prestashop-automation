import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite
import test.PrestaShopTest

@Suite
@SelectClasses(
    PrestaShopTest::class
)
class PrestaShopTestSuite