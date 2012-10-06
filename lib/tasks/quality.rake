# Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
# This work is licensed under http://creativecommons.org/licenses/by-sa/3.0/

# Some of these tools are old and a bit fragile; due to ruby_parser
# and sexp_processor issues, Ruby gets hung if they're run as classes.
# Thus, these tasks are set up to run these tools from the command line.

desc "Run all quality tests"
task :quality => ["quality:best_practices", "quality:flog", "quality:flay", "quality:simplecov"]

namespace :quality do
  # TODO the first time we add an app/helper, add it to this list,
  # TODO add spec/**/*.rb also
  code_files = "app/[cmu]*/*.rb"

  desc "Analyze for Rails best-practices"
  task :best_practices do
    system "rails_best_practices -f html --with-git --output-file=quality/best_practices.html ."
  end
  task :bp => :best_practices

  desc "Analyze for code complexity"
  task :flog do
    system "flog -d #{code_files} >quality/flog.txt"
  end

  desc "Analyze for code duplication"
  task :flay do
    system "flay #{code_files} >quality/flay.txt"
  end

  desc "Analyze for code coverage"
  task :simplecov do
    system "rspec spec"
  end
end
