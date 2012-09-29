require 'factory_girl_rails'

Correction.delete_all
FactoryGirl.create(:correction, :current => 'Call me Ishmeal.')
FactoryGirl.create(:correction, :current => 'It was a bright cold day in April, and the clocks were stricking thirteen.')
FactoryGirl.create(:correction, :current => 'The sky above the port was the color of television, turned to a dead channel.')
