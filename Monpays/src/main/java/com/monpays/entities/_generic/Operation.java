package com.monpays.entities._generic;

import com.monpays.entities._generic.enums.EOperationType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
public class Operation {
    @Enumerated(EnumType.STRING)
    @XmlElement(name = "operationType")
    private EOperationType operation;
    @XmlElement(name = "groupName")
    private String groupName;

    public Operation(EOperationType operation, String groupName) {
        this.operation = operation;
        this.groupName = this.toSentenceCase(groupName);
    }

    public void setGroupName(String groupName) {
        this.groupName = this.toSentenceCase(groupName);
    }

    private String toSentenceCase(String str) {
        StringBuilder result = new StringBuilder();
        String[] components = str.split("\\.");

        for (String component:
             components) {
            result.append(toSentenceCaseWord(component));
            result.append(".");
        }

        return result.substring(0, result.length() - 1);
    }

    private String toSentenceCaseWord(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
