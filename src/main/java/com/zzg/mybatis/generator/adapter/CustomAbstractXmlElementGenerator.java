package com.zzg.mybatis.generator.adapter;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

import java.util.List;

/**
 * Created by BennieSun on 2019/07/30
 **/
public class CustomAbstractXmlElementGenerator extends AbstractXmlElementGenerator {

    @Override
    public void addElements(XmlElement parentElement) {
        StringBuilder sb = new StringBuilder();

        /*****************************************base_column end***************************************************/
        //公共base_column
        XmlElement baseColumnSql = new XmlElement("sql");
        baseColumnSql.addAttribute(new Attribute("id", "base_column"));
        //在这里添加where条件
        XmlElement baseColumnElement = new XmlElement("trim"); //设置trim标签
        baseColumnElement.addAttribute(new Attribute("suffixOverrides", ",")); //去掉后缀的多余and
        List<IntrospectedColumn> baseColumnKeys = introspectedTable.getAllColumns();
        for (int j=0;j<baseColumnKeys.size();j++) {
            sb.setLength(0);
            sb.append("`");
            sb.append(baseColumnKeys.get(j).getJavaProperty());
            sb.append("`, ");
            baseColumnElement.addElement(new TextElement(sb.toString()));
        }
        baseColumnSql.addElement(baseColumnElement);
        parentElement.addElement(baseColumnSql);

        // 公用include base_column
        XmlElement includeBaseColumn = new XmlElement("include");
        includeBaseColumn.addAttribute(new Attribute("refid", "base_column"));
        /*****************************************base_column end***************************************************/

        /******************************************privateKey start**************************************************/
        //公共primaryKey,添加where条件
        XmlElement privateKeySql = new XmlElement("sql");
        privateKeySql.addAttribute(new Attribute("id", "base_primary_key"));
        //在这里添加where条件
        XmlElement primaryKeyElement = new XmlElement("trim"); //设置trim标签
        primaryKeyElement.addAttribute(new Attribute("prefix", "WHERE"));
        primaryKeyElement.addAttribute(new Attribute("suffixOverrides", "and")); //去掉后缀的多余and
        List<IntrospectedColumn> primaryKeys = introspectedTable.getPrimaryKeyColumns();
        for (int j=0;j<primaryKeys.size();j++) {
            sb.setLength(0);
            sb.append("    `");
            sb.append(primaryKeys.get(j).getJavaProperty());
            sb.append("`");
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(primaryKeys.get(j)));
            sb.append(" and ");
            primaryKeyElement.addElement(new TextElement(sb.toString()));
        }
        privateKeySql.addElement(primaryKeyElement);
        parentElement.addElement(privateKeySql);

        // 公用include base_primary_key
        XmlElement includePrimaryKey = new XmlElement("include");
        includePrimaryKey.addAttribute(new Attribute("refid", "base_primary_key"));
        /*****************************************privateKey end***************************************************/

        /******************************************base_query start**************************************************/
        // 增加base_query
        XmlElement sql = new XmlElement("sql");
        sql.addAttribute(new Attribute("id", "base_query"));
        //在这里添加where条件
        XmlElement selectTrimElement = new XmlElement("trim"); //设置trim标签
        selectTrimElement.addAttribute(new Attribute("prefix", "WHERE"));
        selectTrimElement.addAttribute(new Attribute("prefixOverrides", "AND | OR")); //添加去掉and or

        for(IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            XmlElement selectNotNullElement = new XmlElement("if"); //$NON-NLS-1$
            sb.setLength(0);
            sb.append("null != ");
            sb.append(introspectedColumn.getJavaProperty());

            sb.append("&& '' != ");
            sb.append(introspectedColumn.getJavaProperty());

            selectNotNullElement.addAttribute(new Attribute("test", sb.toString()));
            sb.setLength(0);
            // 添加and
            sb.append(" and ");
            // 添加别名t
            sb.append("t.");
            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            // 添加等号
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            selectNotNullElement.addElement(new TextElement(sb.toString()));
            selectTrimElement.addElement(selectNotNullElement);
        }
        sql.addElement(selectTrimElement);
        parentElement.addElement(sql);

        // 公用include base_query
        XmlElement include = new XmlElement("include");
        include.addAttribute(new Attribute("refid", "base_query"));
        /******************************************base_query start**************************************************/



        // 公用select
        sb.setLength(0);
        sb.append("select ");
        sb.append("t.* ");
        sb.append("from ");
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        sb.append(" t");
        TextElement selectText = new TextElement(sb.toString());

        // 增加find
        XmlElement find = new XmlElement("select");
        find.addAttribute(new Attribute("id", "find"));
        find.addAttribute(new Attribute("resultMap", introspectedTable.getBaseRecordType()));
        find.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));
        find.addElement(selectText);
        find.addElement(include);
        parentElement.addElement(find);

        // 增加findAll
        XmlElement list = new XmlElement("select");
        list.addAttribute(new Attribute("id", "findAll"));
        list.addAttribute(new Attribute("resultMap", introspectedTable.getBaseRecordType()));
        list.addElement(selectText);
        parentElement.addElement(list);


        // 公用insert
        XmlElement insetElement = new XmlElement("insert");
        sb.setLength(0);
        sb.append("INSERT INTO ");
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        sb.append("( ");
        insetElement.addElement(new TextElement(sb.toString()));
        insetElement.addElement(includeBaseColumn);
        sb.setLength(0);
        sb.append(") ");
        sb.append("VALUES( ");
        insetElement.addElement(new TextElement(sb.toString()));
        List<IntrospectedColumn> columnsInsertList = introspectedTable.getAllColumns();
        for(int i=0;i< columnsInsertList.size();i++) {
            sb.setLength(0);
            sb.append(MyBatis3FormattingUtilities.getParameterClause(columnsInsertList.get(i)));
            if (i!=columnsInsertList.size()-1){
                sb.append(",");
            }
            insetElement.addElement(new TextElement(sb.toString()));
        }
        sb.setLength(0);
        sb.append(")");
        insetElement.addAttribute(new Attribute("id", "saveOne"));
        insetElement.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));
        insetElement.addElement(new TextElement(sb.toString()));
        parentElement.addElement(insetElement);



        // 公用update
        XmlElement update = new XmlElement("update");
        sb.setLength(0);
        sb.append("update ");
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        sb.append(" set ");
        update.addElement(new TextElement(sb.toString()));

        List<IntrospectedColumn> columnsList = introspectedTable.getAllColumns();
        StringBuilder sbUdgment = new StringBuilder();
        for(int i=0;i< columnsList.size();i++) {
            XmlElement updateNotNullElement = new XmlElement("if"); //$NON-NLS-1$
            sbUdgment.setLength(0);
            sbUdgment.append("null != ");
            sbUdgment.append(columnsList.get(i).getJavaProperty());
            sbUdgment.append(" and '' != ");
            sbUdgment.append(columnsList.get(i).getJavaProperty());
            updateNotNullElement.addAttribute(new Attribute("test", sbUdgment.toString()));

            sbUdgment.setLength(0);
            sbUdgment.append("`");
            sbUdgment.append(MyBatis3FormattingUtilities.getEscapedColumnName(columnsList.get(i)));
            sbUdgment.append("`");
            sbUdgment.append(" = ");
            sbUdgment.append(MyBatis3FormattingUtilities.getParameterClause(columnsList.get(i)));
            if (i!=columnsList.size()-1){
                sbUdgment.append(",");
            }
            updateNotNullElement.addElement(new TextElement(sbUdgment.toString()));
            update.addElement(updateNotNullElement);
        }

        update.addAttribute(new Attribute("id", "update"));
        update.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));
        update.addElement(includePrimaryKey);
        parentElement.addElement(update);
    }

}
