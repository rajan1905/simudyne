package entity;

import lombok.Getter;

@Getter
public class AgentYearResult {
    private int breedCLost;
    private int breedCGained;
    private int breedCRegained;

    public void breedCLost(){
        breedCLost++;
    }

    public void breedCGained(){
        breedCGained++;
    }

    public void breedCRegained(){
        breedCRegained++;
    }

    @Override
    public String toString(){
        return " [ BreedC Gained : " +breedCGained + " , BreedCLost : " + breedCLost +
                " , BreedCRegained : "+breedCRegained + " ]";
    }
}
