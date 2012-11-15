<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html"/>
  <xsl:template match="/">
    <html>
    <body>
    <table>
    <tr>
    <xsl:apply-templates select="webRowSet/metadata/column-definition" />
    </tr>
    <xsl:apply-templates select="webRowSet/data/currentRow" />
    </table>
    </body>
    </html>
  </xsl:template>
  <xsl:template match="column-definition">
    <xsl:for-each select="column-label">
      <td><xsl:value-of select="."/></td>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="currentRow">
    <tr>
      <xsl:for-each select="columnValue">
        <td><xsl:value-of select="."/></td>
      </xsl:for-each>
    </tr>
  </xsl:template>
</xsl:stylesheet>