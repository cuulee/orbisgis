//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.06.02 at 07:13:07 PM CEST 
//


package org.orbisgis.core.renderer.persistance.se;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ImageOutlineType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ImageOutlineType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.opengis.net/se}AreaSymbolizer"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ImageOutlineType", propOrder = {
    "areaSymbolizer"
})
public class ImageOutlineType {

    @XmlElement(name = "AreaSymbolizer")
    protected AreaSymbolizerType areaSymbolizer;

    /**
     * Gets the value of the areaSymbolizer property.
     * 
     * @return
     *     possible object is
     *     {@link AreaSymbolizerType }
     *     
     */
    public AreaSymbolizerType getAreaSymbolizer() {
        return areaSymbolizer;
    }

    /**
     * Sets the value of the areaSymbolizer property.
     * 
     * @param value
     *     allowed object is
     *     {@link AreaSymbolizerType }
     *     
     */
    public void setAreaSymbolizer(AreaSymbolizerType value) {
        this.areaSymbolizer = value;
    }

}
