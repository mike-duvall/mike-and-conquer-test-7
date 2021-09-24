package main

import spock.lang.Specification

class DummyTests extends Specification {


    def "Parse log file"() {
        given:
        String filePath = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\CnCRemastered\\log\\CnCDLL_Log_16000.txt"

        when:
        File file = new File(filePath)
        String line
        file.withReader { reader ->
            while ((line = reader.readLine()) != null) {
                if(line.contains("Entering CNC_Advance_Instance()")) {
                    line = line.substring(13,36)
                    line = line.replace(",", ".")
                    println "${line}"
                }

            }
        }

        then:
        true
    }

}
