package dev.gotenberg.common

import spock.lang.Specification


class CommonUtilsSpecification extends Specification {
    def "should be truthy when the url is valid"() {
        when:
        def url = "https://gotenberg.dev/"
        boolean isValid = CommonUtils.isValidURL(url)
        then:
        isValid == true
    }

    def "should be falsy when the url is invalid"() {
        when:
        def url = "htt://gotenberg.dev/"
        boolean isValid = CommonUtils.isValidURL(url)
        then:
        isValid == false
    }

    def "should be truthy when the file is index.html"() {
        given:
        File file = Mock({
            isFile() >> true
            getName() >> "index.html"
        })
        when:
        boolean isIndexFile = CommonUtils.isIndex(file)
        then:
        isIndexFile == true
    }

    def "should be falsy when the file is not index.html"() {
        given:
        File file = Mock({
            isFile() >> true
            getName() >> "file.pdf"
        })
        when:
        boolean isIndexFile = CommonUtils.isIndex(file)
        then:
        isIndexFile == false
    }

    def "should be truthy when the file is supported"() {
        given:
        File file = Mock({
            isFile() >> true
            getName() >> "file.docx"
        })
        when:
        boolean isSupportedFile = CommonUtils.isSupported(file)
        then:
        isSupportedFile == true
    }

    def "should be falsy when the file is not supported"() {
        given:
        File file = Mock({
            isFile() >> true
            getName() >> "file.gql"
        })
        when:
        boolean isSupportedFile = CommonUtils.isSupported(file)
        then:
        isSupportedFile == false
    }

    def "should be truthy when the file is a markdown"() {
        given:
        File file = Mock({
            isFile() >> true
            getName() >> "markdown.md"
        })
        when:
        boolean isMarkdownFile = CommonUtils.isMarkdown(file)
        then:
        isMarkdownFile == true
    }

    def "should be falsy when the file is not a markdown"() {
        given:
        File file = Mock({
            isFile() >> true
            getName() >> "markdown.txt"
        })
        when:
        boolean isMarkdownFile = CommonUtils.isMarkdown(file)
        then:
        isMarkdownFile == false
    }

    def "should be truthy when the file is a PDF"() {
        given:
        File file = Mock({
            isFile() >> true
            getName() >> "file.pdf"
        })
        when:
        boolean isPdfFile = CommonUtils.isPDF(file)
        then:
        isPdfFile == true
    }

    def "should be falsy when the file is not a PDF"() {
        given:
        File file = Mock({
            isFile() >> true
            getName() >> "file.txt"
        })
        when:
        boolean isPdfFile = CommonUtils.isPDF(file)
        then:
        isPdfFile == false
    }

    def "should be truthy when index.html exists in the file list"() {
        given:
        File file = Mock({
            isFile() >> true
            getName() >> "index.html"
        })
        List<File> files = new ArrayList<>()
        files.add(file)
        when:
        boolean containsIndexFile = CommonUtils.containsIndex(files)
        then:
        containsIndexFile == true
    }

    def "should be falsy when index.html does not exist in the file list"() {
        given:
        File file = Mock({
            isFile() >> true
            getName() >> "file.html"
        })
        List<File> files = new ArrayList<>()
        files.add(file)
        when:
        boolean containsIndexFile = CommonUtils.containsIndex(files)
        then:
        containsIndexFile == false
    }
}