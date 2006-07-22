#!/usr/bin/python
from HyperText.HTML import BR, TABLE, TR, TH, TD, EM, quote_body
from HyperText.Documents import Document

from LunarcPage import LunarcPage

class HyperTextTest(LunarcPage):
    
    def writeContent(self):
        
        s = "font-family: tahoma; font-size: 10pt;"
        
        t = TABLE()
        tr = TR(TH(BR(), style=s))
        for i in range(16): tr.append(TH(hex(i), style=s))
        t.append(tr)
        
        for j in range(0,128,16):
            tr = TR(TH(hex(j)),style=s)
            t.append(tr)
            for i in range(16):
                v = i+j
                tr.append(TD(EM(v), BR(), quote_body(repr(chr(v)))))
        
        self.write(t)
