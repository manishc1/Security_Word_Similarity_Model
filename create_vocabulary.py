#! /usr/bin/python


import sys
import os


class Vocab_Creator(object):
    """
    Class that creates the vocabulary.
    """

    def __init__(self, dir_name, vocab_file):
        """
        Initializes the class.
        """
        self.dir_name = dir_name
        self.vocab_file = vocab_file
        self.raw_vocab = {}
        self.tagged_vocab = []


    def create_vocab(self):
        """
        Creates the vocabulary.
        """
        count = 0
        f = open('/home/manish/lushan/nlp/model/cs_vocabulary.txt', 'r')
        lines = [line.strip() for line in f]
        f.close()
        for line in lines:
            self.raw_vocab[line] = True

        for rootdir, subdir, filenames in os.walk(self.dir_name):
            for filename in filenames:
                count = count + 1
                if (filename.endswith('.possf2')):
                    with open (str(rootdir) + '/' + filename, "r") as f:
                        tagged_text = f.read().replace('\n', '')
                        for tagged_word in tagged_text.split(' '):
                            if (self.raw_vocab.has_key(tagged_word.split('_')[0])):
                                self.tagged_vocab.append(tagged_word)

        self.tagged_vocab = sorted(list(set(self.tagged_vocab)))
        with open(self.vocab_file, "w") as f:
            f.write(str(len(self.tagged_vocab)) + '\n' + '\n'.join(self.tagged_vocab))


def read_line(filename):
    f = open(filename, 'r')
    lines = [line for line in f]
    f.close()
    newlines = []
    for line in lines:
        line = line.lower().strip()
        if ((len(line) == 0) or (line[0] == '#')):
            continue
        if (line[0:2] == '//'):
            line = line.split(' ', 1)[1]        
        newlines.append(line.replace(' ', ''))
        newlines = newlines + line.split(' ')
    return newlines


def form_vocab():
    """
    form vocab
    """
    vocab = read_line('/home/manish/Dropbox/Thesis/DySeCor/config/computer_science_terms-1.cfg') + \
        read_line('/home/manish/Dropbox/Thesis/DySeCor/config/computer_science_terms-2.cfg') + \
        read_line('/home/manish/Dropbox/Thesis/DySeCor/config/computer_science_terms-3.cfg') + \
        read_line('/home/manish/Dropbox/Thesis/DySeCor/config/computer_science_terms-4.cfg') + \
        read_line('/home/manish/Dropbox/Thesis/DySeCor/config/nist_glossary_of_key_information_security_terms.cfg') + \
        read_line('/home/manish/Dropbox/Thesis/DySeCor/config/security_terms.cfg')
    vocab = sorted(list(set(vocab)))
    with open('/home/manish/lushan/nlp/model/cs_vocabulary.txt', "w") as f:
        f.write('\n'.join(vocab))


def main(dir_name, vocab_file):
    """
    Entry point of the program.
    """
    #form_vocab()
    #exit()
    if (not os.path.isdir(dir_name)):
        print dir_name + ': No such directory'
        exit(0)
    if (os.path.exists(vocab_file)):
        print vocab_file + ': Already exists'
        exit(0)
    vc = Vocab_Creator(dir_name, vocab_file)
    vc.create_vocab()


if __name__ == "__main__":
    nargs = len(sys.argv)
    args = sys.argv
    if nargs == 3:
        main(args[1], args[2])
    else:
        print "Usage: python create_vocabulary.py <path_to_diractory> <path_to_vocab_file>"

