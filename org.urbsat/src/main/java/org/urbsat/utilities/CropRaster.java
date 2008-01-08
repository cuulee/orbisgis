package org.urbsat.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.indexes.SpatialIndex;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.source.Source;
import org.gdms.source.SourceManager;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.strategies.FirstStrategy;
import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.OperationException;
import org.grap.utilities.EnvelopeUtil;

import com.vividsolutions.jts.geom.LinearRing;

/*
 * select CropRaster('MNT_Nantes_Lambert') from fence ;
 *
 */

public class CropRaster implements CustomQuery {
	private final static File tmpPath = new File(System
			.getProperty("user.home")
			+ "/OrbisGIS.tif");

	/*
	 * This CustomQuery needs to be rewritten using : UPDATE ds SET the_geom =
	 * addZDEM(RasterLayerAlias) WHERE ...;
	 */

	private GeoRaster geoRaster;

	private ObjectMemoryDriver driver;

	private String outDsName;

	private String path;

	public DataSource evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values) throws ExecutionException {
		if (tables.length != 1) {
			throw new ExecutionException(
					"CropRaster only operates on one table");
		}
		if (1 != values.length) {
			throw new ExecutionException(
					"CropRaster only operates with onevalue, the raster");
		}

		try {
			final Source dem = dsf.getSourceManager().getSource(
					values[0].toString());
			int type = dem.getType();
			if ((type & SourceManager.RASTER) == SourceManager.RASTER) {
				if (dem.isFileSource()) {
					path = dem.getFile().getAbsolutePath();
					geoRaster = GeoRasterFactory.createGeoRaster(path);
					geoRaster.open();
				} else {
					throw new ExecutionException("The DEM must be a file !");
				}
			} else {
				throw new UnsupportedOperationException(
						"Cannot understand source type: " + type);
			}

			final SpatialDataSourceDecorator inSds = new SpatialDataSourceDecorator(
					tables[0]);
			inSds.open();

			// built the driver for the resulting datasource and register it...
			driver = new ObjectMemoryDriver(new String[] { "index", "the_geom",
					"path" }, new Type[] { TypeFactory.createType(Type.INT),
					TypeFactory.createType(Type.GEOMETRY),
					TypeFactory.createType(Type.STRING), });
			outDsName = dsf.getSourceManager().nameAndRegister(driver);

			LinearRing polygon = (LinearRing) EnvelopeUtil.toGeometry(inSds
					.getFullExtent());

			geoRaster.crop(polygon).save(tmpPath.getAbsolutePath());

			driver
					.addValues(new Value[] { ValueFactory.createValue(0),
							ValueFactory.createValue(polygon),
							ValueFactory.createValue(path) });

			dsf.getSourceManager().register("croppedRaster", tmpPath);

			inSds.cancel();

			// spatial index for the ds
			dsf.getIndexManager().buildIndex(outDsName, "the_geom",
					SpatialIndex.SPATIAL_INDEX);
			FirstStrategy.indexes = true;
			return dsf.getDataSource(outDsName);

		} catch (FileNotFoundException e) {
			throw new ExecutionException(e);
		} catch (IOException e) {
			throw new ExecutionException(e);
		} catch (GeoreferencingException e) {
			throw new ExecutionException(e);
		} catch (DriverException e) {
			throw new ExecutionException(e);
		} catch (IndexException e) {
			throw new ExecutionException(e);
		} catch (NoSuchTableException e) {
			throw new ExecutionException(e);
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		} catch (DataSourceCreationException e) {
			throw new ExecutionException(e);
		} catch (OperationException e) {
			throw new ExecutionException(e);
		}
	}

	public String getDescription() {
		return "This custom query produces a new DEM and new Datasource with 3 fields : id, the_geom (geometry), path";
	}

	public String getSqlOrder() {
		return "select CropRaster('the_DEM', the_geom) from myTable;";
	}

	public String getName() {
		return "CropRaster";
	}
}