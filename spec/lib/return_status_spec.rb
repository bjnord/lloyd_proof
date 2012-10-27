# Author::     Brent J. Nordquist <mailto:brent@nordist.net>
# Copyright::  Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
# License::    {CC Attribution-ShareAlike 3.0 Unported}[http://creativecommons.org/licenses/by-sa/3.0/]

require 'spec_helper'

# FIXME shouldn't use specific class Correction; should spec
# generically as in <http://www.ruby-forum.com/topic/214968>

describe ReturnStatus do

  shared_examples_for "a successful status" do
    it { should include(:sync_id => sync_id, :status => :ok) }
  end

  shared_examples_for "a status with errors" do
    it { should include(:sync_id => sync_id, :status => :error) }
    its([:errors]) { should_not be_empty }
  end

  describe '#create_and_return_status' do
    let(:correction_attr) { Hash[:sync_id => sync_id] }
    let(:sync_id) { 444 }
    subject { Correction.create_and_return_status(correction_attr) }

    context 'with a valid object' do
      before(:each) { Correction.should_receive(:create).and_return(mock_model(Correction)) }

      it_behaves_like "a successful status"
    end

    context 'with an invalid object' do
      let(:errors) { stub("errors", :full_messages => ['fnord']) }
      before(:each) { Correction.should_receive(:create).and_return(mock_model(Correction, :persisted? => false, :errors => errors)) }

      it_behaves_like "a status with errors"
    end

    context 'with a mass-assignment attempt' do
      before(:each) { Correction.should_receive(:create).and_raise(ActiveModel::MassAssignmentSecurity::Error) }

      it_behaves_like "a status with errors"
    end
  end

  describe '#save_and_return_status' do
    let(:correction) { Correction.new }
    let(:sync_id) { 333 }
    subject { correction.save_and_return_status(sync_id) }

    context 'with a valid object' do
      before(:each) { correction.should_receive(:save).and_return(true) }

      it_behaves_like "a successful status"
    end

    context 'with an invalid object' do
      let(:errors) { stub("errors", :full_messages => ['fnord']) }
      before(:each) do
        correction.should_receive(:save).and_return(false)
        correction.should_receive(:errors).and_return(errors)
      end

      it_behaves_like "a status with errors"
    end
  end

end
