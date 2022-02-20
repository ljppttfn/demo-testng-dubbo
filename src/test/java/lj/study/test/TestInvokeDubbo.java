package lj.study.test;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.testng.annotations.Test;

/**
 * dubbo 泛化调用示例
 */
public class TestInvokeDubbo {

    @Test
    public void test(){
        ReferenceConfig<GenericService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setApplication(new ApplicationConfig("test"));
        referenceConfig.setRegistry(new RegistryConfig("zookeeper://127.0.0.1:2181"));
        referenceConfig.setInterface("lj.demo.dubbo.provider.service.impl.ByeService");
        referenceConfig.setGeneric(true);
        GenericService genericService = referenceConfig.get();

        Object result = genericService.$invoke(
                "method1",
                new String[]{"java.lang.String"},
                new Object[]{"1234"});

        System.out.println(result);
    }
}
