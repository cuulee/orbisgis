//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.06.02 at 07:13:07 PM CEST 
//


package org.orbisgis.core.renderer.persistance.ogc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.orbisgis.core.renderer.persistance.se.UnitaryOperatorType;


/**
 * <p>Java class for ExpressionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExpressionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExpressionType")
@XmlSeeAlso({
    org.orbisgis.core.renderer.persistance.se.FunctionType.class,
    UnitaryOperatorType.class,
    BinaryOperatorType.class,
    org.orbisgis.core.renderer.persistance.ogc.FunctionType.class
})
public abstract class ExpressionType {


}
