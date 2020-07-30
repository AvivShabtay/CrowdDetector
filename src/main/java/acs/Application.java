package acs;

import io.sentry.Sentry;
import io.sentry.event.BreadcrumbBuilder;
import io.sentry.event.UserBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Controller
@SpringBootApplication
public class Application {

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World!";
    }

    @Bean
    public WebMvcConfigurer corsRegistry() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                    .addMapping("/**")
                    .allowedOrigins("http://localhost:8000", "https://crowd-detector.netlify.app");
            }
        };
    }

    /**
     * An example method that throws an exception.
     */
    static void unsafeMethod() {
        throw new UnsupportedOperationException("You shouldn't call this!");
    }

    public static void main(String[] args) {
        Sentry.init("https://49108bfa5b1d4c93a2e5772222d9a4e7@o397071.ingest.sentry.io/5251243");

        SpringApplication.run(Application.class, args);

        try {
            Sentry.getContext().addExtra("Extra info", "info");
            Sentry.getContext().setUser(new UserBuilder().setEmail("alon.bukai@s.afeka.ac.il").build());
            Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder().setMessage("Server started up").build());
            unsafeMethod();
        } catch (Exception e) {
            // This sends an exception event to Sentry using the statically stored instance
            // that was created in the ``main`` method.
            Sentry.capture(e);
        }
    }
}
