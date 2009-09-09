package liquibase.serializer.core.string;

import liquibase.change.*;
import liquibase.change.core.*;
import liquibase.change.custom.CustomChangeWrapper;
import liquibase.change.custom.CustomSqlChange;
import liquibase.change.custom.ExampleCustomSqlChange;
import liquibase.logging.Logger;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import liquibase.test.JUnitResourceAccessor;
import static org.junit.Assert.*;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.*;

public class StringChangeLogSerializerTest {

    @Test
    public void serialized_CustomChange() throws Exception {

        String expectedString = "customChange:[\n" +
                "    className=\"liquibase.change.custom.ExampleCustomSqlChange\"\n" +
                "    paramValues={\n" +
                "        columnName=\"column_name\",\n" +
                "        newValue=\"new_value\",\n" +
                "        tableName=\"table_name\"\n" +
                "    }\n" +
                "]";

        CustomChangeWrapper wrapper = new CustomChangeWrapper();
        wrapper.setFileOpener(new ClassLoaderResourceAccessor());
        //wrapper.setFileOpener(new JUnitResourceAccessor());
        //wrapper.setClassLoader(new JUnitResourceAccessor().toClassLoader());
        wrapper.setClassLoader(getClass().getClassLoader());
        wrapper.setClass("liquibase.change.custom.ExampleCustomSqlChange");
        wrapper.setParam("columnName", "column_name");
        wrapper.setParam("newValue", "new_value");
        wrapper.setParam("tableName", "table_name");

        assertEquals(expectedString, new StringChangeLogSerializer().serialize(wrapper));
    }

    @Test
    public void serialized_AddColumnChange() {
        AddColumnChange change = new AddColumnChange();

        assertEquals("addColumn:[\n" +
                "    columns=[]\n" +
                "]", new StringChangeLogSerializer().serialize(change));

        change.setTableName("TABLE_NAME");

        assertEquals("addColumn:[\n" +
                "    columns=[]\n" +
                "    tableName=\"TABLE_NAME\"\n" +
                "]", new StringChangeLogSerializer().serialize(change));

        change.setSchemaName("SCHEMA_NAME");
        assertEquals("addColumn:[\n" +
                "    columns=[]\n" +
                "    schemaName=\"SCHEMA_NAME\"\n" +
                "    tableName=\"TABLE_NAME\"\n" +
                "]", new StringChangeLogSerializer().serialize(change));

        ColumnConfig column = new ColumnConfig();
        change.addColumn(column);
        column.setName("COLUMN_NAME");

        assertEquals("addColumn:[\n" +
                "    columns=[\n" +
                "        column:[\n" +
                "            name=\"COLUMN_NAME\"\n" +
                "        ]\n" +
                "    ]\n" +
                "    schemaName=\"SCHEMA_NAME\"\n" +
                "    tableName=\"TABLE_NAME\"\n" +
                "]", new StringChangeLogSerializer().serialize(change));

        ColumnConfig column2 = new ColumnConfig();
        change.addColumn(column2);
        column2.setName("COLUMN2_NAME");
        column2.setAutoIncrement(true);
        column2.setValueNumeric(52);

        assertEquals("addColumn:[\n" +
                "    columns=[\n" +
                "        column:[\n" +
                "            name=\"COLUMN_NAME\"\n" +
                "        ],\n" +
                "        column:[\n" +
                "            autoIncrement=\"true\"\n" +
                "            name=\"COLUMN2_NAME\"\n" +
                "            valueNumeric=\"52\"\n" +
                "        ]\n" +
                "    ]\n" +
                "    schemaName=\"SCHEMA_NAME\"\n" +
                "    tableName=\"TABLE_NAME\"\n" +
                "]", new StringChangeLogSerializer().serialize(change));
    }

    @Test
    public void serialized_AddForeignKeyConstraint() {
        AddForeignKeyConstraintChange change = new AddForeignKeyConstraintChange();

        assertEquals("addForeignKeyConstraint:[]", new StringChangeLogSerializer().serialize(change));

        change.setBaseTableName("TABLE_NAME");
        change.setBaseColumnNames("COL1, COL2");
        change.setBaseTableSchemaName("BASE_SCHEM");
        change.setConstraintName("FK_TEST");
        change.setDeferrable(true);
        change.setInitiallyDeferred(true);
        change.setDeleteCascade(true);
        change.setOnDelete("SET NULL");
        change.setOnUpdate("NO ACTION");
        change.setReferencedTableName("REF_TABLE");
        change.setReferencedColumnNames("COLA, COLB");
        change.setReferencedTableSchemaName("REF_SCHEM");

        assertEquals("addForeignKeyConstraint:[\n" +
                "    baseColumnNames=\"COL1, COL2\"\n" +
                "    baseTableName=\"TABLE_NAME\"\n" +
                "    baseTableSchemaName=\"BASE_SCHEM\"\n" +
                "    constraintName=\"FK_TEST\"\n" +
                "    deferrable=\"true\"\n" +
                "    initiallyDeferred=\"true\"\n" +
                "    onDelete=\"SET NULL\"\n" +
                "    onUpdate=\"NO ACTION\"\n" +
                "    referencedColumnNames=\"COLA, COLB\"\n" +
                "    referencedTableName=\"REF_TABLE\"\n" +
                "    referencedTableSchemaName=\"REF_SCHEM\"\n" +
                "]", new StringChangeLogSerializer().serialize(change));

    }
    @Test
    public void serialized_AddUniqueKeyConstraint() {
    	AddUniqueConstraintChange change = new AddUniqueConstraintChange();

        assertEquals("addUniqueConstraint:[]", new StringChangeLogSerializer().serialize(change));

        change.setTableName("TABLE_NAME");
        change.setColumnNames("COL1, COL2");
        change.setSchemaName("BASE_SCHEM");
        change.setConstraintName("FK_TEST");
        change.setDeferrable(true);
        change.setInitiallyDeferred(true);
        change.setDisabled(true);
        change.setTablespace("TABLESPACE1");

        assertEquals("addUniqueConstraint:[\n" +
                "    columnNames=\"COL1, COL2\"\n" +
                "    constraintName=\"FK_TEST\"\n" +
                "    deferrable=\"true\"\n" +
                "    disabled=\"true\"\n" +
                "    initiallyDeferred=\"true\"\n" +
                "    schemaName=\"BASE_SCHEM\"\n" +
                "    tableName=\"TABLE_NAME\"\n" +
                "    tablespace=\"TABLESPACE1\"\n" +
                "]", new StringChangeLogSerializer().serialize(change));

    }
//    @Test
//    public void serialized_changeSet() {
//        ChangeSet changeSet = new ChangeSet("1", "ted", true, false, "com/example/test.xml", "c:/com/exmple/test", "context1, context2", "mysql, oracle");
//        AddColumnChange change = new AddColumnChange();
//        changeSet.addChange(change);
//
//        assertEquals("changeSet:[\n" +
//                "    alwaysRun=\"true\"\n" +
//                "    author=\"ted\"\n" +
//                "    contextList=\"context1,context2\"\n" +
//                "    dbmsList=\"mysql,oracle\"\n" +
//                "    filePath=\"com/example/test.xml\"\n" +
//                "    id=\"1\"\n" +
//                "    physicalFilePath=\"c:/com/example/test.xml\"\n" +
//                "    runOnChange=\"false\"\n" +
//                "    changes: [\n" +
//                "        addColumn:[\n" +
//                "            columns=[]\n" +
//                "        ]\n" +
//                "    ]\n" +
//                "]", new StringChangeLogSerializer().serialize(changeSet));
//    }

    @Test
    public void serialized_SQLFileChange() {
        SQLFileChange change = new SQLFileChange();

        assertEquals("sqlFile:[]", new StringChangeLogSerializer().serialize(change));

        change.setPath("PATH/TO/File.txt");

        assertEquals("sqlFile:[\n" +
                "    path=\"PATH/TO/File.txt\"\n" +
                "]", new StringChangeLogSerializer().serialize(change));
    }


    @Test
    public void tryAllChanges() throws Exception {
        for (Class<? extends Change> changeClass : ChangeFactory.getInstance().getRegistry().values()) {
            Change change = changeClass.getConstructor().newInstance();

            setFields(change);

            String string = new StringChangeLogSerializer().serialize(change);
//            System.out.println(string);
//            System.out.println("-------------");
            assertTrue("@ in string.  Probably poorly serialzed object reference." + string, string.indexOf("@") < 0);
        }
    }

    private void setFields(Object object) throws Exception {
        Class clazz = object.getClass();
        if (clazz.getName().indexOf(".ext.") > 0) {
            return; //don't worry about ext samples
        }
        
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getAnnotation(ChangeProperty.class) != null && !field.getAnnotation(ChangeProperty.class).includeInSerialization()) {
                continue;
            }
            field.setAccessible(true);
            if (field.getType().getName().equals("[[Z")) {
                //nothing, from emma
            } else if (field.getName().equals("serialVersionUID")) {
                //nothing
            } else if (field.getType().equals(Logger.class)) {
                //nothing
            } else if (field.getType().equals(ResourceAccessor.class)) {
                //nothing
            } else if (field.getType().equals(ClassLoader.class)) {
                //nothing
            } else if (field.getType().equals(long.class)) {
                field.set(object, createInteger().longValue());
            } else if (field.getType().equals(String.class)) {
                field.set(object, createString());
            } else if (field.getType().equals(Number.class)) {
                field.set(object, createNumber());
            } else if (field.getType().equals(Integer.class)) {
                field.set(object, createInteger());
            } else if (field.getType().equals(BigInteger.class)) {
                field.set(object, createBigInteger());
            } else if (field.getType().equals(Date.class)) {
                field.set(object, createDate());
            } else if (field.getType().equals(Boolean.class)) {
                field.set(object, createBoolean());
            } else if (field.getType().equals(ColumnConfig.class)) {
                field.set(object, createColumnConfig());
            } else if (field.getType().equals(ConstraintsConfig.class)) {
                field.set(object, createConstraintsConfig());
            } else if (field.getType().getName().equals("liquibase.change.custom.CustomChange")) {
                field.set(object, createCustomChange());
            } else if (field.getType().equals(Map.class)) {
                field.set(object, createMap());
            } else if (Collection.class.isAssignableFrom(field.getType())) {
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType) {
                    int genericsLength = ((ParameterizedType) genericType).getActualTypeArguments().length;
                    if (genericsLength == 1) {
                        Class typeToCreate = (Class) ((ParameterizedType) genericType).getActualTypeArguments()[0];
                        Collection collection;
                        if (field.getType().equals(List.class)) {
                            collection = new ArrayList();
                        } else if (field.getType().equals(SortedSet.class)) {
                            collection = new TreeSet();
                        } else {
                            throw new RuntimeException("Unknow collection type: " + field.getType().getName());
                        }
                        if (typeToCreate.equals(ColumnConfig.class)) {
                            collection.add(createColumnConfig());
                            collection.add(createColumnConfig());
                        } else if (typeToCreate.equals(LoadDataColumnConfig.class)) {
                            collection.add(createLoadDataColumnConfig());
                            collection.add(createLoadDataColumnConfig());
                        } else if (typeToCreate.equals(String.class)) {
                            collection.add(createString());
                            collection.add(createString());
                        } else {
                            throw new RuntimeException("Unknown generic type for " + clazz.getName() + "." + field.getName() + ": " + typeToCreate.getName());
                        }
                        field.set(object, collection);
                    } else {
                        throw new RuntimeException("Found " + genericsLength + " generics for " + clazz.getName() + "." + field.getName());
                    }
                } else {
                    fail("List not generic");
                }
            } else {
                fail("Unknown field type in " + clazz.getName() + ": " + field.getType().getName());
            }
        }

    }

    private LoadDataColumnConfig createLoadDataColumnConfig() throws Exception {
        LoadDataColumnConfig config = new LoadDataColumnConfig();
        setFields(config);
        return config;
    }

    private String createString() {
        return Long.toString(Math.abs(new Random().nextLong()), 36);
    }

    private Number createNumber() {
        return new Random().nextDouble() * 10000;
    }

    private Integer createInteger() {
        return new Random().nextInt();
    }

    private BigInteger createBigInteger() {

        return new BigInteger(20, new Random());
    }

    private Date createDate() {
        return new Date(new Random().nextLong());
    }

    private Boolean createBoolean() {
        return true;
    }

    private Map createMap() {
        Map map = new HashMap();
        map.put(createString(), createString());
        map.put(createString(), createString());
        map.put(createString(), createString());
        return map;
    }

    private ColumnConfig createColumnConfig() throws Exception {
        ColumnConfig config = new ColumnConfig();
        setFields(config);
        return config;
    }

    private ConstraintsConfig createConstraintsConfig() throws Exception {
        ConstraintsConfig config = new ConstraintsConfig();
        setFields(config);
        return config;
    }

    private CustomSqlChange createCustomChange() throws Exception {
        CustomSqlChange config = new ExampleCustomSqlChange();
        setFields(config);
        return config;
    }
}
