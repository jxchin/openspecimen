//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.4-b18-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.03.23 at 12:30:40 CDT 
//


package edu.wustl.cider.jaxb.domain;


/**
 * Java content class for SpecimenCharacteristicsType complex type.
 * <p>The following schema fragment specifies the expected content contained within this java content object. (defined at file:/C:/Users/pkalantri/Documents/caTissue/specimen_new.xsd line 90)
 * <p>
 * <pre>
 * &lt;complexType name="SpecimenCharacteristicsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="tissueSite" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
public interface SpecimenCharacteristicsType {


    /**
     * Gets the value of the tissueSite property.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String}
     */
    java.lang.String getTissueSite();

    /**
     * Sets the value of the tissueSite property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    void setTissueSite(java.lang.String value);

}
