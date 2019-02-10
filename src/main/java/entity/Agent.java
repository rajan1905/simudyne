package entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Agent {
    private String agentBreed;
    private String policyId;
    private int age;
    private int socialGrade;
    private int paymentAtPurchase;
    private float attributeBrand;
    private float attributePrice;
    private float attributePromotions;
    private boolean autoRenew;
    private int inertiaForSwitch;
}
