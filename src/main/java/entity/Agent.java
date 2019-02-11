package entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Agent {
    private AgentBreed agentBreed;
    private String policyId;
    private int age;
    private int socialGrade;
    private int paymentAtPurchase;
    private double attributeBrand;
    private double attributePrice;
    private double attributePromotions;
    private boolean autoRenew;
    private int inertiaForSwitch;

    @Override
    public Agent clone(){
        Agent agentForYear = new Agent();
        agentForYear.agentBreed = agentBreed;
        agentForYear.policyId = policyId;
        agentForYear.age = age;
        agentForYear.socialGrade = socialGrade;
        agentForYear.paymentAtPurchase = paymentAtPurchase;
        agentForYear.attributeBrand = attributeBrand;
        agentForYear.attributePrice = attributePrice;
        agentForYear.attributePromotions = attributePromotions;
        agentForYear.autoRenew = autoRenew;
        agentForYear.inertiaForSwitch = inertiaForSwitch;

        return agentForYear;
    }

    @Override
    public String toString(){
        return "[ Policy id : "+policyId+ ", AgentBreed: "+agentBreed.name()+" ]";
    }

}
