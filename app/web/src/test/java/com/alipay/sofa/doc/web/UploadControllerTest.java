package com.alipay.sofa.doc.web;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
public class UploadControllerTest {
    /*
    curl -i -X POST -H 'Content-Type: multipart/form-data' \
-F "file=@test-doc.zip" \
-F "yuqueNamespace=zhanggeng.zg/whyya9" \
-F "gitRepo=http://gitlab.alipay-inc.com/zhanggeng.zg/test-doc.git" \
-F "gitDocRoot=/doc" \
-F "slugGenMode=DIRS_FILENAME" \
-F "header=heeeeeeeeed1019" \
-F "yuqueUser=zhanggeng.zg" \
http://127.0.0.1:8888/v1/rest/syncByZip
     */

        /*
    curl -i -X POST -H 'Content-Type: multipart/form-data' \
-F "file=@test-doc.zip" \
-F "yuqueNamespace=zhanggeng.zg/whyya9" \
-F "gitRepo=http://gitlab.alipay-inc.com/zhanggeng.zg/test-doc.git" \
-F "yuqueUser=zhanggeng.zg" \
http://127.0.0.1:8888/v1/rest/syncByZip


    这种我需要提前配置 yuqueUser 的 token 到我这边的平台。
     */

     /*
    curl -i -X POST -H 'Content-Type: multipart/form-data' \
-F "file=@test-doc.zip" \
-F "yuqueNamespace=zhanggeng.zg/whyya9" \
-F "gitRepo=http://gitlab.alipay-inc.com/zhanggeng.zg/test-doc.git" \
-F "yuqueToken=xxxxxxxxxxxxxxx" \
http://sofadoc.alipay.com/v1/rest/syncByZip

    file 就是你的文档git库的压缩包
    xxxxxxxxxxxxxxx 换成你的语雀团队token
     */

}
