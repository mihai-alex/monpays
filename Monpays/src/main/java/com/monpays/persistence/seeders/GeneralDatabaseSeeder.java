package com.monpays.persistence.seeders;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@org.springframework.context.annotation.Profile({"dev", "prod"})
public class GeneralDatabaseSeeder extends AbstractDatabaseSeeder implements CommandLineRunner {

    @Override
    public void run(String... args) {
        this.insertSystemEntriesIfNotPresent();

        this.insertProfilesIfNotPresent();
        this.insertUsersIfNotPresent();
        this.insertAccountsIfNotPresent();
    }
}
