package org.orbisgis.wpsservice.model;

import net.opengis.wps._2_0.Format;
import org.junit.Assert;
import org.junit.Test;
import org.orbisgis.wpsgroovyapi.attributes.*;
import org.orbisgis.wpsservice.controller.utils.FormatFactory;
import org.orbisgis.wpsservice.controller.utils.ObjectAnnotationConverter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *@author Sylvain PALOMINOS
 */
public class WpsModelTest {
    /** Field containing the DataFieldAttribute annotation. */
    @DataFieldAttribute(
            dataStoreFieldName = "data store title",
            fieldTypes = {"GEOMETRY", "NUMBER"},
            excludedTypes = {"MULTILINESTRING", "LONG"},
            multiSelection = true
    )
    public Object dataFieldInput;

    /**
     * Test if the decoding and convert of the DataField annotation into its java object is valid.
     */
    @Test
    public void testDataFieldAttributeConvert(){
        try {
            boolean annotationFound = false;
            //Retrieve the DataField object
            DataField datafield = null;
            //Inspect all the annotation of the field to get the DescriptionTypeAttribute one
            Field dataFieldField = this.getClass().getDeclaredField("dataFieldInput");
            for(Annotation annotation : dataFieldField.getDeclaredAnnotations()){
                //Once the annotation is get, decode it.
                if(annotation instanceof DataFieldAttribute){
                    annotationFound = true;
                    DataFieldAttribute descriptionTypeAnnotation = (DataFieldAttribute) annotation;
                    Format format = FormatFactory.getFormatFromExtension(FormatFactory.TEXT_EXTENSION);
                    format.setDefault(true);
                    datafield = ObjectAnnotationConverter.annotationToObject(descriptionTypeAnnotation, format,
                            URI.create("datastore:uri"));
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if(!annotationFound || datafield == null){
                Assert.fail("Unable to get the annotation '@DataFieldAttribute' from the field.");
            }

            ////////////////////////
            // Test the DataField //
            ////////////////////////

            String errorMessage = "Error, the DataField 'dataStoreUri' field should be 'datastore:uri' instead of "+
                    datafield.getDataStoreIdentifier().toString();
            Assert.assertEquals(errorMessage, URI.create("datastore:uri"), datafield.getDataStoreIdentifier());

            errorMessage = "Error, the DataField 'isMultiSelection' field should be 'true' instead of "+
                    datafield.isMultiSelection();
            Assert.assertTrue(errorMessage, datafield.isMultiSelection());

            errorMessage = "Error, the DataField 'isSourceModified' field should be 'true' instead of "+
                    datafield.isSourceModified();
            Assert.assertTrue(errorMessage, datafield.isMultiSelection());

            errorMessage = "Error, the DataField 'getExcludedTypeList' field should contain two value : " +
                    "'MULTILINESTRING' and 'LONG'.";
            boolean condition = datafield.getExcludedTypeList().size() == 2 &&
                    datafield.getExcludedTypeList().contains(DataType.MULTILINESTRING) &&
                    datafield.getExcludedTypeList().contains(DataType.LONG);
            Assert.assertTrue(errorMessage, condition);

            errorMessage = "Error, the DataField 'getFieldTypeList' field should contain two value : " +
                    "'GEOMETRY' and 'NUMBER'.";
            condition = datafield.getFieldTypeList().size() == 2 &&
                    datafield.getFieldTypeList().contains(DataType.GEOMETRY) &&
                    datafield.getFieldTypeList().contains(DataType.NUMBER);
            Assert.assertTrue(errorMessage, condition);


        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field from the class '" +
                    this.getClass().getCanonicalName()+"'.");
        }

    }



    /** Field containing the DataStoreAttribute annotation. */
    @DataStoreAttribute(
            dataStoreTypes = {"GEOMETRY", "NUMBER"},
            excludedTypes = {"MULTILINESTRING", "LONG"}
    )
    public Object dataStoreInput;

    /**
     * Test if the decoding and convert of the DataStore annotation into its java object is valid.
     */
    @Test
    public void testDataStoreAttributeConvert(){
        try {
            boolean annotationFound = false;
            //Retrieve the DataField object
            DataStore dataStore = null;
            //Inspect all the annotation of the field to get the DescriptionTypeAttribute one
            Field dataStoreField = this.getClass().getDeclaredField("dataStoreInput");
            for(Annotation annotation : dataStoreField.getDeclaredAnnotations()){
                //Once the annotation is get, decode it.
                if(annotation instanceof DataStoreAttribute){
                    annotationFound = true;
                    DataStoreAttribute descriptionTypeAnnotation = (DataStoreAttribute) annotation;
                    List<Format> format = new ArrayList<>();
                    format.add(FormatFactory.getFormatFromExtension(FormatFactory.TEXT_EXTENSION));
                    format.get(0).setDefault(true);
                    dataStore = ObjectAnnotationConverter.annotationToObject(descriptionTypeAnnotation, format);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if(!annotationFound || dataStore == null){
                Assert.fail("Unable to get the annotation '@DataFieldAttribute' from the field.");
            }

            ////////////////////////
            // Test the DataStore //
            ////////////////////////

            String errorMessage = "Error, the dataStore 'excludedTypeList' field should contain two value : " +
                    "'MULTILINESTRING' and 'LONG'.";
            boolean condition = dataStore.getExcludedTypeList().size() == 2 &&
                    dataStore.getExcludedTypeList().contains(DataType.MULTILINESTRING) &&
                    dataStore.getExcludedTypeList().contains(DataType.LONG);
            Assert.assertTrue(errorMessage, condition);

            errorMessage = "Error, the dataStore 'dataStoreTypeList' field should contain two value : " +
                    "'GEOMETRY' and 'NUMBER'.";
            condition = dataStore.getDataStoreTypeList().size() == 2 &&
                    dataStore.getDataStoreTypeList().contains(DataType.GEOMETRY) &&
                    dataStore.getDataStoreTypeList().contains(DataType.NUMBER);
            Assert.assertTrue(errorMessage, condition);


        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field from the class '" +
                    this.getClass().getCanonicalName()+"'.");
        }

    }



    /** Field containing the EnumerationAttribute annotation. */
    @EnumerationAttribute(
            multiSelection = true,
            isEditable = true,
            names = {"name1, name2, name3"},
            selectedValues = {"value1, value2"},
            values = {"value1, value2, value3"}
    )
    public Object enumerationInput;

    /**
     * Test if the decoding and convert of the Enumeration annotation into its java object is valid.
     */
    @Test
    public void testEnumerationAttributeConvert(){
        try {
            boolean annotationFound = false;
            //Retrieve the Enumeration object
            Enumeration enumeration = null;
            //Inspect all the annotation of the field to get the EnumerationAttribute one
            Field enumerationField = this.getClass().getDeclaredField("enumerationInput");
            for(Annotation annotation : enumerationField.getDeclaredAnnotations()){
                //Once the annotation is get, decode it.
                if(annotation instanceof EnumerationAttribute){
                    annotationFound = true;
                    EnumerationAttribute descriptionTypeAnnotation = (EnumerationAttribute) annotation;
                    Format format = FormatFactory.getFormatFromExtension(FormatFactory.TEXT_EXTENSION);
                    format.setDefault(true);
                    enumeration = ObjectAnnotationConverter.annotationToObject(descriptionTypeAnnotation, format);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if(!annotationFound || enumeration == null){
                Assert.fail("Unable to get the annotation '@EnumerationAttribute' from the field.");
            }

            ////////////////////////
            // Test the DataField //
            ////////////////////////

            String errorMessage = "Error, the enumeration 'isMultiSelection' field should be 'true' instead of "+
                    enumeration.isMultiSelection();
            Assert.assertTrue(errorMessage, enumeration.isMultiSelection());

            errorMessage = "Error, the enumeration 'isEditable' field should be 'true' instead of "+
                    enumeration.isEditable();
            Assert.assertTrue(errorMessage, enumeration.isEditable());

            errorMessage = "Error, the enumeration 'values' field should contain three value : " +
                    "'value1', 'value2' and 'value3'.";
            Assert.assertArrayEquals(errorMessage, enumeration.getValues(), new String[]{"value1, value2, value3"});

            errorMessage = "Error, the enumeration 'valuesNames' field should contain two value : " +
                    "'name1', 'name2' and 'name3'.";
            Assert.assertArrayEquals(errorMessage, enumeration.getValuesNames(), new String[]{"name1, name2, name3"});

            errorMessage = "Error, the enumeration 'defaultValues' field should contain three value : " +
                    "'value1' and 'value2'.";
            Assert.assertArrayEquals(errorMessage, enumeration.getDefaultValues(), new String[]{"value1, value2"});


        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field from the class '" +
                    this.getClass().getCanonicalName()+"'.");
        }

    }



    /** Field containing the FieldValueAttribute annotation. */
    @FieldValueAttribute(
            multiSelection = true,
            dataFieldFieldName = "dataFieldTitle"
    )
    public Object fieldValueInput;

    /**
     * Test if the decoding and convert of the FieldValue annotation into its java object is valid.
     */
    @Test
    public void testFieldValueAttributeConvert(){
        try {
            boolean annotationFound = false;
            //Retrieve the FieldValue object
            FieldValue fieldValue = null;
            //Inspect all the annotation of the field to get the FieldValueAttribute one
            Field fieldValueField = this.getClass().getDeclaredField("fieldValueInput");
            for(Annotation annotation : fieldValueField.getDeclaredAnnotations()){
                //Once the annotation is get, decode it.
                if(annotation instanceof FieldValueAttribute){
                    annotationFound = true;
                    FieldValueAttribute descriptionTypeAnnotation = (FieldValueAttribute) annotation;
                    Format format = FormatFactory.getFormatFromExtension(FormatFactory.TEXT_EXTENSION);
                    format.setDefault(true);
                    fieldValue = ObjectAnnotationConverter.annotationToObject(descriptionTypeAnnotation, format,
                            URI.create("uri:datafield"));
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if(!annotationFound || fieldValue == null){
                Assert.fail("Unable to get the annotation '@FieldValueAttribute' from the field.");
            }

            /////////////////////////
            // Test the FieldValue //
            /////////////////////////

            String errorMessage = "Error, the fieldValue 'isDataFieldModified' field should be 'true' instead of "+
                    fieldValue.isDataFieldModified();
            Assert.assertTrue(errorMessage, fieldValue.isDataFieldModified());

            errorMessage = "Error, the fieldValue 'isDataStoreModified' field should be 'true' instead of "+
                    fieldValue.isDataStoreModified();
            Assert.assertTrue(errorMessage, fieldValue.isDataStoreModified());

            errorMessage = "Error, the fieldValue 'multiSelection' field should be 'true' instead of "+
                    fieldValue.getMultiSelection();
            Assert.assertTrue(errorMessage, fieldValue.isDataStoreModified());

            errorMessage = "Error, the fieldValue 'getDataFieldIdentifier' field should be " +
                    URI.create("uri:datafield");
            Assert.assertEquals(errorMessage, fieldValue.getDataFieldIdentifier(), URI.create("uri:datafield"));


        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field from the class '" +
                    this.getClass().getCanonicalName()+"'.");
        }

    }



    /** Field containing the GeometryDataAttribute annotation. */
    @GeometryAttribute(
            dimension = 1,
            geometryTypes = {"FLOAT", "LONG"},
            excludedTypes = {"GEOMETRY", "NUMBER"}
    )
    public Object geometryInput;

    /**
     * Test if the decoding and convert of the Geometry annotation into its java object is valid.
     */
    @Test
    public void testGeometryAttributeConvert(){
        try {
            boolean annotationFound = false;
            //Retrieve the Geometry object
            GeometryData geometry = null;
            //Inspect all the annotation of the field to get the GeometryAttribute one
            Field geometryField = this.getClass().getDeclaredField("geometryInput");
            for(Annotation annotation : geometryField.getDeclaredAnnotations()){
                //Once the annotation is get, decode it.
                if(annotation instanceof GeometryAttribute){
                    annotationFound = true;
                    GeometryAttribute descriptionTypeAnnotation = (GeometryAttribute) annotation;
                    Format format = FormatFactory.getFormatFromExtension(FormatFactory.TEXT_EXTENSION);
                    format.setDefault(true);
                    geometry = ObjectAnnotationConverter.annotationToObject(descriptionTypeAnnotation, format);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if(!annotationFound || geometry == null){
                Assert.fail("Unable to get the annotation '@GeometryAttribute' from the field.");
            }

            ///////////////////////
            // Test the Geometry //
            ///////////////////////

            String errorMessage = "Error, the geometry 'dimension' field should be 1 instead of '"+
                    geometry.getDimension()+"'";
            Assert.assertEquals(errorMessage, geometry.getDimension(), 1);

            errorMessage = "Error, the geometry 'geometryTypeList' field should contain two value : " +
                    "'FLOAT' and 'LONG'";
            boolean condition = geometry.getGeometryTypeList().size() == 2 &&
                    geometry.getGeometryTypeList().contains(DataType.FLOAT) &&
                    geometry.getGeometryTypeList().contains(DataType.LONG);
            Assert.assertTrue(errorMessage, condition);

            errorMessage = "Error, the geometry 'excludedTypeList' field should contain two value : " +
                    "'GEOMETRY' and 'NUMBER'";
            condition = geometry.getExcludedTypeList().size() == 2 &&
                    geometry.getExcludedTypeList().contains(DataType.GEOMETRY) &&
                    geometry.getExcludedTypeList().contains(DataType.NUMBER);
            Assert.assertTrue(errorMessage, condition);


        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field from the class '" +
                    this.getClass().getCanonicalName()+"'.");
        }

    }



    /** Field containing the RawDataAttribute annotation. */
    @RawDataAttribute(
            isDirectory = false,
            isFile = false,
            multiSelection = true
    )
    public Object rawDataInput;

    /**
     * Test if the decoding and convert of the RawData annotation into its java object is valid.
     */
    @Test
    public void testRawDataAttributeConvert(){
        try {
            boolean annotationFound = false;
            //Retrieve the RawData object
            RawData rawData = null;
            //Inspect all the annotation of the field to get the RawDataAttribute one
            Field rawDataField = this.getClass().getDeclaredField("rawDataInput");
            for(Annotation annotation : rawDataField.getDeclaredAnnotations()){
                //Once the annotation is get, decode it.
                if(annotation instanceof RawDataAttribute){
                    annotationFound = true;
                    RawDataAttribute descriptionTypeAnnotation = (RawDataAttribute) annotation;
                    Format format = FormatFactory.getFormatFromExtension(FormatFactory.TEXT_EXTENSION);
                    format.setDefault(true);
                    rawData = ObjectAnnotationConverter.annotationToObject(descriptionTypeAnnotation, format);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if(!annotationFound || rawData == null){
                Assert.fail("Unable to get the annotation '@RawDataAttribute' from the field.");
            }

            //////////////////////
            // Test the RawData //
            //////////////////////

            String errorMessage = "Error, the rawData 'isDirectory' field should be 'false' instead of '"+
                    rawData.isDirectory()+"'";
            Assert.assertFalse(errorMessage, rawData.isDirectory());

            errorMessage = "Error, the rawData 'isFile' field should be 'false' instead of '"+
                    rawData.isFile()+"'";
            Assert.assertFalse(errorMessage, rawData.isFile());

            errorMessage = "Error, the rawData 'multiSelection' field should be 'true' instead of '"+
                    rawData.multiSelection()+"'";
            Assert.assertTrue(errorMessage, rawData.multiSelection());


        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field from the class '" +
                    this.getClass().getCanonicalName()+"'.");
        }

    }
}