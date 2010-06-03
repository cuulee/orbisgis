//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.06.02 at 07:13:07 PM CEST 
//


package org.orbisgis.core.renderer.persistance.se;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DotMapFillType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DotMapFillType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/se}FillType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/se}Graphic"/>
 *         &lt;element ref="{http://www.opengis.net/se}ValuePerMark"/>
 *         &lt;element ref="{http://www.opengis.net/se}ValueToRepresent"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DotMapFillType", propOrder = {
    "graphic",
    "valuePerMark",
    "valueToRepresent"
})
public class DotMapFillType
    extends FillType
{

    @XmlElementRef(name = "Graphic", namespace = "http://www.opengis.net/se", type = JAXBElement.class)
    protected JAXBElement<? extends GraphicType> graphic;
    @XmlElement(name = "ValuePerMark", required = true)
    protected ParameterValueType valuePerMark;
    @XmlElement(name = "ValueToRepresent", required = true)
    protected ParameterValueType valueToRepresent;

    /**
     * Gets the value of the graphic property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link MarkGraphicType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TextGraphicType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PieChartType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AxisChartType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GraphicType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ExternalGraphicType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CompositeGraphicType }{@code >}
     *     
     */
    public JAXBElement<? extends GraphicType> getGraphic() {
        return graphic;
    }

    /**
     * Sets the value of the graphic property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link MarkGraphicType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TextGraphicType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PieChartType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AxisChartType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GraphicType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ExternalGraphicType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CompositeGraphicType }{@code >}
     *     
     */
    public void setGraphic(JAXBElement<? extends GraphicType> value) {
        this.graphic = ((JAXBElement<? extends GraphicType> ) value);
    }

    /**
     * Gets the value of the valuePerMark property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     *     
     */
    public ParameterValueType getValuePerMark() {
        return valuePerMark;
    }

    /**
     * Sets the value of the valuePerMark property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     *     
     */
    public void setValuePerMark(ParameterValueType value) {
        this.valuePerMark = value;
    }

    /**
     * Gets the value of the valueToRepresent property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     *     
     */
    public ParameterValueType getValueToRepresent() {
        return valueToRepresent;
    }

    /**
     * Sets the value of the valueToRepresent property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     *     
     */
    public void setValueToRepresent(ParameterValueType value) {
        this.valueToRepresent = value;
    }

}
