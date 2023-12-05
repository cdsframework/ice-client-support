<?xml version="1.0" encoding="UTF-8"?>
<!--

    The cdsframework support testcase imports project implements some base test case importer functionality.

    Copyright 2016 HLN Consulting, LLC

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    For more information about the this software, see https://www.hln.com/services/open-source/ or send
    correspondence to scm@cdsframework.org.

-->
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="2.0"
    xmlns:previous="org.cdsframework.util.support.data">
    <!-- This sheet replaces the url of a namespace where needed -->
    <xsl:output encoding='UTF-8' indent='yes' method='xml'/>
  <!-- Identity transform -->
  <xsl:template match='@*|node()'>
    <xsl:copy>
      <xsl:apply-templates select='@*|node()'/>
    </xsl:copy>
  </xsl:template>
  <!-- Previous namespace -> current. No other changes required. -->
  <xsl:template match='previous:*'>
    <xsl:element name='{local-name()}' namespace='org.cdsframework.util.support.data.cds.testcase'>
      <xsl:copy-of select='namespace::*[not(. = namespace-uri(current()))]' />
      <xsl:apply-templates select='@* | node()' />
    </xsl:element>
  </xsl:template>
</xsl:stylesheet>