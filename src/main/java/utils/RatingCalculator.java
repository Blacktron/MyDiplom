package utils;

import models.implementation.Candidate;
import models.implementation.Experience;
import models.implementation.Position;
import models.implementation.Requirement;

import java.util.Arrays;
import java.util.List;

/**
 * @Created by Terrax on 31-Jan-2016.
 */
public class RatingCalculator {
    private int[] priorityCount;
    private int highShare = 0;
    private int medShare = 0;
    private int lowShare = 0;
    private Position position;
    List<Requirement> requirements;

    public RatingCalculator() {
        priorityCount = new int[] {0, 0, 0};
        highShare = 0;
        medShare = 0;
        lowShare = 0;
        position = null;
        requirements = null;
    }

    public RatingCalculator(Position position) {
        priorityCount = new int[] {0, 0, 0};
        highShare = 0;
        medShare = 0;
        lowShare = 0;
        this.position = position;
        requirements = position.getRequirements();
    }

    public void getRequirementPrioritiesOfPosition() {
        //System.out.println("Requirements count: " + requirements.size());
        for (Requirement requirement : requirements) {
            //System.out.println("Req priority: " + requirement.getPriority());
            priorityCount[requirement.getPriority() - 1]++;
            //System.out.println("Priority counts: " + Arrays.toString(priorityCount));
        }
    }

    public void makeTheShares() {
        // If there are only high priority requirements.
        if (priorityCount[0] > 0 && priorityCount[1] == 0 && priorityCount[2] == 0) {
            highShare = 100;
        }
        // If there are only medium priority requirements.
        else if (priorityCount[0] == 0 && priorityCount[1] > 0 && priorityCount[2] == 0) {
            medShare = 100;
        }
        // If there are only low priority requirements.
        else if (priorityCount[0] == 0 && priorityCount[1] == 0 && priorityCount[2] > 0) {
            lowShare = 100;
        }
        // If there is a requirement having all three priorities.
        else if (priorityCount[0] > 0 && priorityCount[1] > 0 && priorityCount[2] > 0) {
            highShare = 70;
            medShare = 20;
            lowShare = 10;
        }
        // If there is a requirement with only high and medium priorities.
        else if (priorityCount[0] > 0 && priorityCount[1] > 0 && priorityCount[2] == 0) {
            highShare = 80;
            medShare = 20;
        }
        // If there is a requirement with only medium and low priorities.
        else if (priorityCount[0] == 0 && priorityCount[1] > 0 && priorityCount[2] > 0) {
            medShare = 70;
            lowShare = 30;
        }
        // If there is a requirement with only high and low priorities.
        else if (priorityCount[0] == 0 && priorityCount[1] > 0 && priorityCount[2] > 0) {
            highShare = 90;
            lowShare = 10;
        }
    }

    public void calculatePoints() {
        getRequirementPrioritiesOfPosition();
        makeTheShares();
        //System.out.println("hi: " + highShare + "me: " + medShare + "lo: " + lowShare);
        List<Candidate> candidates = position.getApplicants();

        for (Candidate candidate : candidates) {
            int rating = 0;
            List<Experience> experiences = candidate.getExperiences();

            for (Requirement requirement : requirements) {
                String technologyName = requirement.getTechnologyName();
                int years = requirement.getYears();
                int priority = requirement.getPriority();

                for (Experience experience : experiences) {
                    String candidateTechExp = experience.getTechnologyName();
                    int candidateYearsExp = experience.getYears();
                    int highReqPoints;
                    int medReqPoints;
                    int lowReqPoints;

                    if (technologyName.equals(candidateTechExp)) {
                        //System.out.println("REQUIREMENT == EXPERIENCE");
                        //System.out.println(technologyName + " " + candidateTechExp);
                        if (priority == 1) {
                            highReqPoints = highShare / priorityCount[0];
                            //rating += ((highReqPoints / years) * candidateYearsExp) / years;
                            rating += highReqPoints * candidateYearsExp / years;
                        } else if (priority == 2) {
                            medReqPoints = medShare / priorityCount[1];
                            //rating += ((medReqPoints / years) * candidateYearsExp) / years;
                            rating += medReqPoints * candidateYearsExp / years;
                        } else if (priority == 3) {
                            lowReqPoints = lowShare / priorityCount[2];
                            //rating += ((lowReqPoints / years) * candidateYearsExp) / years;
                            rating += lowReqPoints * candidateYearsExp / years;
                        }
                    }
                }
            }
            System.out.println("RATING: " + rating);
            candidate.setRating(rating);
        }

        //position.setApplicants(candidates);
    }
}