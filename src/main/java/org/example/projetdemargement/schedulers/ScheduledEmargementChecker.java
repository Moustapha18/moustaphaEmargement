package org.example.projetdemargement.schedulers;

import org.example.projetdemargement.models.Cours;
import org.example.projetdemargement.services.CoursService;
import org.example.projetdemargement.services.EmargementService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledEmargementChecker {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final EmargementService emargementService = new EmargementService();
    private final CoursService coursService = new CoursService();

    public void lancerVerificationAutomatique(Long professeurId) {
        List<Cours> coursList = coursService.getCoursByProfesseur(professeurId);

        for (Cours cours : coursList) {
            LocalDateTime startTime = LocalDateTime.of(cours.getDate(), cours.getHeure_Debut());
            long delay = Duration.between(LocalDateTime.now(), startTime.plusMinutes(30)).toMillis();

            if (delay > 0) {
                scheduler.schedule(() -> {
                    boolean alreadyEmarged = emargementService.hasAlreadyEmarged(professeurId, cours.getId());
                    if (!alreadyEmarged) {
                        emargementService.marquerAbsent(professeurId, cours.getId());
                        System.out.println("⏰ Emargement non fait : professeur marqué absent pour le cours " + cours.getNom());
                    }
                }, delay, TimeUnit.MILLISECONDS);
            }
        }
    }

    public void arreter() {
        scheduler.shutdown();
    }
}
