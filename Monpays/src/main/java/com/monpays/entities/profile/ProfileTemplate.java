package com.monpays.entities.profile;

import com.monpays.entities._generic.Operation;
import com.monpays.entities.profile.enums.EProfileType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class ProfileTemplate {
    @XmlElement(name = "type")
    protected EProfileType type;

    @XmlElementWrapper(name = "rights")
    @XmlElement(name = "operation")
    protected List<Operation> rights;
}
