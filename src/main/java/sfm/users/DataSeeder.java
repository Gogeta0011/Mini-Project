package sfm.users;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {
    private final RoleRepository roles;

    public DataSeeder(RoleRepository roles) {
        this.roles = roles;
    }

    @Override
    public void run(String... args) {
        for (RoleName rn : RoleName.values()) {
            roles.findByName(rn).orElseGet(() -> roles.save(new Role(rn)));
        }
    }
}
