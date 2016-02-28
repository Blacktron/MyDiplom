package app;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import services.*;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * @Created by Terrax on 23-Sep-2015.
 */
public class MyApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(CandidateService.class);
        classes.add(CompanyService.class);
        classes.add(HRService.class);
        classes.add(PositionService.class);
        classes.add(TechnologyService.class);
        classes.add(JacksonJsonProvider.class);

        return classes;
    }
}