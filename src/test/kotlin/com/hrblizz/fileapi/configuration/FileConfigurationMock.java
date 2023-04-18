package configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileConfigurationMock {

    @Bean
    public Path uploadPath() {
        return Paths.get("uploads");
    }
}
