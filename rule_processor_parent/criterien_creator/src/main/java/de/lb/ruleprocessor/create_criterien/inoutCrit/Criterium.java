/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.lb.ruleprocessor.create_criterien.inoutCrit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author gerschmann
 */
public class Criterium {

        String name;
        String type;
        String description;
        String displayName;
        String tooltip;
        String accessMethod;
        List<Tooltip> tooltips;
        public Criterium(){
            
        }
        public Criterium(@NotNull String pName){
            name = pName;


        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setCriterionType(String type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    public String getDisplayName() {
        return displayName == null?name:displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public List<Tooltip> getTooltips() {
        if(tooltips != null){
            Collections.sort(tooltips);
            return tooltips;
        }
        return new ArrayList<>();
    }

    public void setTooltips(List<Tooltip> tooltips) {
        this.tooltips = tooltips;
    }
    
    public void addTooltip(Tooltip tooltip){
        if(tooltips == null){
            tooltips = new ArrayList<>();
        }
        tooltips.add(tooltip);
    }

    public String getAccessMethod() {
        return accessMethod;
    }

    public void setAccessMethod(String accessMethod) {
        this.accessMethod = accessMethod;
    }
    
    
}
    
    
